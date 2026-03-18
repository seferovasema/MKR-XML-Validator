package az.afb.mkrvalidator.controller;

import az.afb.mkrvalidator.service.CreditValidatorService;
import az.afb.mkrvalidator.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mkr")
@RequiredArgsConstructor
public class CreditController {

    private final CreditValidatorService creditValidatorService;
    private final StatisticsService statisticsService;

    @PostMapping("/validate")
    public List<String> validate(@RequestParam MultipartFile file) {
        return creditValidatorService.validateCredit(file);
    }

    @PostMapping("/statistica")
    public Map<String, Object> statistics(@RequestParam MultipartFile file) {
        return statisticsService.getStatistics(file);
    }
}