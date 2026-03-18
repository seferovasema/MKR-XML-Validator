
package az.afb.mkrvalidator.service;

import az.afb.mkrvalidator.util.ValidationUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CreditValidatorService {

    public List<String> validateCredit(MultipartFile file) {
        List<String> result = new ArrayList<>();
        try {
            byte[] bytes = file.getBytes();

            Map<String, Integer> lineMap = buildLineMap(bytes);

            XmlMapper mapper = new XmlMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            JsonNode root = mapper.readTree(bytes);
            
            String reportingDate = "";
            JsonNode headerNode = root.get("Header");
            if (headerNode != null && !headerNode.isNull()) {
                reportingDate = getText(headerNode, "ReportingDate");
            }

            JsonNode creditsNode = root.get("Credits");
            if (creditsNode == null || creditsNode.isNull()) {
                result.add("XML-də <Credits> elementi tapılmadı");
                return result;
            }

            JsonNode creditNode = creditsNode.get("Credit");
            if (creditNode == null || creditNode.isNull()) {
                result.add("XML-də heç bir <Credit> tapılmadı");
                return result;
            }

            List<JsonNode> creditList = new ArrayList<>();
            if (creditNode.isArray()) {
                creditNode.forEach(creditList::add);
            } else {
                creditList.add(creditNode);
            }

            int creditIndex = 1;
            for (JsonNode c : creditList) {
                String cKey = "credit_" + creditIndex;

                JsonNode borrower = c.get("Borrower");
                if (borrower == null || borrower.isNull()) {
                    int line = lineMap.getOrDefault(cKey, 0);
                    result.add("Line:" + line + " | Credit #" + creditIndex + ": Borrower məlumatı yoxdur");
                    creditIndex++;
                    continue;
                }
                String idVal = getText(borrower, "id");
                if (idVal.isEmpty()) {
                    int line = lineMap.getOrDefault(cKey + ".Borrower.id", lineMap.getOrDefault(cKey, 0));
                    result.add("Line " + line + " | Credit #" + creditIndex + ": <id> boş ola bilməz");
                    creditIndex++;
                    continue;
                }

                boolean isVoen = idVal.matches("\\d{10}");
                boolean isFiziki = !isVoen || !idVal.endsWith("1");

                if (isFiziki) {
                    if (isEmpty(borrower, "name")) {
                        int line = lineMap.getOrDefault(cKey + ".Borrower.name", lineMap.getOrDefault(cKey, 0));
                        result.add("Line:" + line + " | Credit #" + creditIndex
                                + ": Fiziki şəxs — <name>  boş ola bilməz (id: " + idVal + ")");
                    }

                    String dob = getText(borrower, "DateOfBirth");
                    if (dob.isEmpty()) {
                        int line = lineMap.getOrDefault(cKey + ".Borrower.DateOfBirth", lineMap.getOrDefault(cKey, 0));
                        result.add("Line:" + line + " | Credit #" + creditIndex
                                + ": Fiziki şəxs — <DateOfBirth>  boş ola bilməz (Borrower id: " + idVal + ")");
                    } else if (!ValidationUtil.isValidDate(dob)) {
                        int line = lineMap.getOrDefault(cKey + ".Borrower.DateOfBirth", lineMap.getOrDefault(cKey, 0));
                        result.add("Line:" + line + " | Credit #" + creditIndex
                                + ": <DateOfBirth> formatı səhvdir, DD/MM/YYYY gözlənilir: " + dob);
                    }

                    if (isEmpty(borrower, "PlaceOfBirth")) {
                        int line = lineMap.getOrDefault(cKey + ".Borrower.PlaceOfBirth", lineMap.getOrDefault(cKey, 0));
                        result.add("Line:" + line + " | Credit #" + creditIndex
                                + ": Fiziki şəxs — <PlaceOfBirth>  boş ola bilməz (Borrower id: " + idVal + ")");
                    }

                    if (isEmpty(borrower, "PinCode")) {
                        int line = lineMap.getOrDefault(cKey + ".Borrower.PinCode", lineMap.getOrDefault(cKey, 0));
                        result.add("Line:" + line + " | Credit #" + creditIndex
                                + ": Fiziki şəxs — <PinCode> boş ola bilməz (Borrower id: " + idVal + ")");
                    }
                }

                String statusCode = getText(c, "CreditStatusCode");
                String disout = getText(c, "DisoutAmountOfCredit");
                if ("007".equals(statusCode)) {
                    try {
                        double disoutVal = disout.isEmpty() ? 0 : Double.parseDouble(disout);
                        if (disoutVal == 0) {
                            int line = lineMap.getOrDefault(cKey + ".DisoutAmountOfCredit", lineMap.getOrDefault(cKey, 0));
                            result.add("Line:" + line + " | Credit #" + creditIndex
                                    + ": <CreditStatusCode> 007 olduqda <DisoutAmountOfCredit> sıfır ola bilməz");
                        }
                    } catch (NumberFormatException e) {
                        int line = lineMap.getOrDefault(cKey + ".DisoutAmountOfCredit", lineMap.getOrDefault(cKey, 0));
                        result.add("Line:" + line + " | Credit #" + creditIndex
                                + ": <DisoutAmountOfCredit> rəqəm deyil: " + disout);
                    }
                }

                if (!reportingDate.isEmpty()) {
                    String dateOfGrant = getText(c, "DateOfGrant");
                    if (!dateOfGrant.isEmpty() && dateOfGrant.equals(reportingDate) && "001".equals(statusCode)) {
                        int line = lineMap.getOrDefault(cKey + ".DateOfGrant", lineMap.getOrDefault(cKey, 0));
                        result.add("Line:" + line + " | Credit #" + creditIndex
                                + ": Borrower id: " + idVal + " — dünən kredit verilib və bağlanıb"
                                + " (DateOfGrant = ReportingDate = " + reportingDate + ")");
                    }
                }

                creditIndex++;
            }

        } catch (Exception e) {
            result.add("XML parsing xətası: " + e.getMessage());
        }

        if (result.isEmpty())
            result.add("Bütün Credit-lər düzgün və validdir");

        return result;
    }

    private Map<String, Integer> buildLineMap(byte[] xmlBytes) throws Exception {
        Map<String, Integer> map = new LinkedHashMap<>();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(xmlBytes));

        int creditCount = 0;
        boolean insideCredit = false;
        boolean insideBorrower = false;
        String currentCreditKey = "";

        while (reader.hasNext()) {
            int event = reader.next();

            if (event == XMLStreamConstants.START_ELEMENT) {
                String tag = reader.getLocalName();
                int line = reader.getLocation().getLineNumber();

                if ("Credit".equals(tag)) {
                    creditCount++;
                    insideCredit = true;
                    insideBorrower = false;
                    currentCreditKey = "credit_" + creditCount;
                    map.put(currentCreditKey, line);

                } else if (insideCredit && "Borrower".equals(tag)) {
                    insideBorrower = true;

                } else if (insideCredit && insideBorrower) {
                    map.put(currentCreditKey + ".Borrower." + tag, line);

                } else if (insideCredit) {
                    map.put(currentCreditKey + "." + tag, line);
                }

            } else if (event == XMLStreamConstants.END_ELEMENT) {
                String tag = reader.getLocalName();
                if ("Borrower".equals(tag)) insideBorrower = false;
                if ("Credit".equals(tag)) { insideCredit = false; insideBorrower = false; }
            }
        }
        reader.close();
        return map;
    }

    private String getText(JsonNode node, String field) {
        JsonNode f = node.get(field);
        if (f == null || f.isNull()) return "";
        String val = f.asText("").trim();
        return "null".equalsIgnoreCase(val) ? "" : val;
    }

    private boolean isEmpty(JsonNode node, String field) {
        return getText(node, field).isEmpty();
    }
}