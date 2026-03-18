package az.afb.mkrvalidator.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    public Map<String, Object> getStatistics(MultipartFile file) {
        Map<String, Object> stats = new LinkedHashMap<>();
        try {
            byte[] bytes = file.getBytes();

            XmlMapper mapper = new XmlMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(bytes);

            String reportingDate = "";
            JsonNode headerNode = root.get("Header");
            if (headerNode != null && !headerNode.isNull()) {
                JsonNode rd = headerNode.get("ReportingDate");
                if (rd != null && !rd.isNull()) {
                    reportingDate = rd.asText("").trim();
                }
            }

            JsonNode creditsNode = root.get("Credits");
            if (creditsNode == null || creditsNode.isNull()) {
                stats.put("xeta", "XML-də <Credits> elementi tapılmadı");
                return stats;
            }

            JsonNode creditNode = creditsNode.get("Credit");
            if (creditNode == null || creditNode.isNull()) {
                stats.put("Ümumi kreditlərin sayı", 0);
                stats.put("Dünən verilmiş kreditlərin sayı", 0);
                stats.put("Bağlanmış kreditlərin sayı", 0);
                return stats;
            }

            List<JsonNode> creditList = new ArrayList<>();
            if (creditNode.isArray()) {
                creditNode.forEach(creditList::add);
            } else {
                creditList.add(creditNode);
            }

            int umumiSay     = creditList.size();
            int dunenSay     = 0;
            int baglanmisSay = 0;

            for (JsonNode c : creditList) {

                JsonNode dogNode = c.get("DateOfGrant");
                if (dogNode != null && !dogNode.isNull()) {
                    String dateOfGrant = dogNode.asText("").trim();
                    if (!dateOfGrant.isEmpty() && dateOfGrant.equals(reportingDate)) {
                        dunenSay++;
                    }
                }

                JsonNode scNode = c.get("CreditStatusCode");
                if (scNode != null && !scNode.isNull()) {
                    String statusCode = scNode.asText("").trim();
                    if ("001".equals(statusCode)) {
                        baglanmisSay++;
                    }
                }
            }

            stats.put("Ümumi kreditlərin sayı", umumiSay);
            stats.put("Dünən verilmiş kreditlərin sayı", dunenSay);
            stats.put("Bağlanmış kreditlərin sayı", baglanmisSay);

        } catch (Exception e) {
            stats.put("xeta", "XML parsing xətası: " + e.getMessage());
        }

        return stats;
    }
}