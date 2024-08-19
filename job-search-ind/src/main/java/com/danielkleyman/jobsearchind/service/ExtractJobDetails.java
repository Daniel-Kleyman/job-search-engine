package com.danielkleyman.jobsearchind.service;


import com.danielkleyman.jobsearchapi.service.AIService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class ExtractJobDetails {
    public final AIService aiService;
    int jobsVisibleOnPage;

    public ExtractJobDetails() {
        this.aiService = new AIService(new RestTemplate());
        this.jobsVisibleOnPage = 0;
    }

    public void extractJobDetails(WebDriver driver, WebDriverWait wait, Map<String, List<String>> jobDetails) {

        extractProcess(driver, wait, jobDetails);

    }

    private void extractProcess(WebDriver driver, WebDriverWait wait, Map<String, List<String>> jobDetails) {
        String pageSource = driver.getPageSource();
        // Define the regex pattern to find the embedded JSON data
        String regexPattern = "window.mosaic.providerData\\[\"mosaic-provider-jobcards\"\\]=(\\{.+?\\});";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(pageSource);

        if (matcher.find()) {
            String jsonString = matcher.group(1);

            // Parse the JSON data
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode rootNode = objectMapper.readTree(jsonString);
                JsonNode resultsNode = rootNode.path("metaData").path("mosaicProviderJobCardsModel").path("results");

                //  Print or process the extracted data
                // System.out.println("Extracted Results Node:");
                // System.out.println(resultsNode.toPrettyString());

                // If you want to see individual job listings or specific fields, iterate through the resultsNode
                if (resultsNode.isArray()) {
                    for (JsonNode jobNode : resultsNode) {
                        // Extract and print job details
                        String jobTitle = jobNode.path("displayTitle").asText();
                        String companyName = jobNode.path("company").asText();
                        String city = jobNode.path("jobLocationCity").asText();
                        String url = jobNode.path("link").asText();
                        String jobDescription = jobNode.path("snippet").asText();
                        String jobKey = jobNode.path("jobkey").asText();

                        System.out.println("Job Title: " + jobTitle);
                        System.out.println("Company: " + companyName);
                        System.out.println("City: " + city);
                        System.out.println("URL: " + url);
                        System.out.println("Description: " + jobDescription);
                        System.out.println("Job Key: " + jobKey);
                        System.out.println("-------------------------------");
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No embedded data found.");
        }
    }
//    private void extractJobDetails(WebDriver driver, WebDriverWait wait, String jobKey) {
//        // Navigate to the full job details page using the jobKey
//        String url = "https://www.indeed.com/m/basecamp/viewjob?viewtype=embedded&jk=" + jobKey;
//        driver.get(url);
//
//        // Wait for the page to load and the _initialData to be present
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
//
//        // Get the page source
//        String pageSource = driver.getPageSource();
//
//        // Define the regex pattern to find the embedded JSON data
//        String regexPattern = "_initialData=(\\{.+?\\});";
//        Pattern pattern = Pattern.compile(regexPattern);
//        Matcher matcher = pattern.matcher(pageSource);
//
//        if (matcher.find()) {
//            String jsonString = matcher.group(1);
//
//            // Parse the JSON data
//            ObjectMapper objectMapper = new ObjectMapper();
//            try {
//                JsonNode rootNode = objectMapper.readTree(jsonString);
//                JsonNode jobInfoWrapperNode = rootNode.path("jobInfoWrapperModel");
//                JsonNode jobInfoModelNode = jobInfoWrapperNode.path("jobInfoModel");
//
//                // Extract and print job details
//                String jobTitle = jobInfoModelNode.path("title").asText();
//                String companyName = jobInfoModelNode.path("company").asText();
//                String city = jobInfoModelNode.path("location").asText();
//                String url = jobInfoModelNode.path("url").asText();
//                String jobDescription = jobInfoModelNode.path("description").asText();
//
//                System.out.println("Job Title: " + jobTitle);
//                System.out.println("Company: " + companyName);
//                System.out.println("City: " + city);
//                System.out.println("URL: " + url);
//                System.out.println("Description: " + jobDescription);
//                System.out.println("-------------------------------");
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            System.out.println("No embedded data found.");
//        }
//    }
    private static boolean filterTitle(String jobTitle) {
        Set<String> excludeKeywords = Set.of(
                "senior", "lead", "leader", "devops", "manager", "qa", "mechanical", "infrastructure", "integration", "civil",
                "principal", "customer", "embedded", "system", " verification", "electrical", "support", "complaint", "solution", "solutions", "simulation", "technical",
                "manufacturing", "validation", "finops", "hardware", "devsecops", "motion", "machine Learning", "design", "sr.", "quality", "architect", "head",
                "director", "president", "executive", "detection", "industrial", "chief", "specialist", "algorithm", "architecture", "admin", " researcher",
                " data science", "webmaster", "medical", "associate", "mrb", "accountant", "waiter", "dft", "test", "musicologist", "sales", "media", "product",
                "reliability", "account", "representative", "Architect", "Analyst", "Account", "Executive", "Specialist", "Associate", "devtest", "big data", "digital",
                "coordinator", "intern", "researcher", "network", "security", "malware", " intelligence", " algo-dev", "electro-optics", "secops", "implementer",
                "ml", "picker", "revenue", "controller", "פלנר", "טכנאי", "emulation", "tester", "counsel", "administrative", "assistant", "production", " scientist",
                "penetration", " investigations", "intelligence", "hrbp", "officer", "curriculum", " business", "team", "staff", "automation");
        Set<String> includeKeywords = Set.of(
                "developer", "engineer", "programmer", "backend", "back-end", "back end", "fullstack", "full-stack", "full stack",
                "software", "fs", "java", "מתחנת", "מפתח"
        );

        // Check if any exclude keyword is present in the job title
        boolean shouldExclude = excludeKeywords.stream()
                .anyMatch(jobTitle::contains);

        // Check if any include keyword is present in the job title
//        boolean shouldInclude = includeKeywords.stream()
//                .anyMatch(keyword -> jobTitle.contains(keyword));
// Check if any include keyword is present in the job title
        boolean shouldInclude = includeKeywords.stream()
                .anyMatch(keyword -> jobTitle.toLowerCase().contains(keyword.toLowerCase()));
        //   return !shouldExclude && shouldInclude;
        return !shouldExclude;
    }

    private static boolean filterDescription(String aboutJob) {
        String aboutJob1 = aboutJob.toLowerCase();
        Set<String> includeKeywords = Set.of("java", "spring", "microservice", "react", "javascript", "oop",
                "typescript", "backend", "back-end", "back end", "fullstack", "full-stack", "full stack"
        );

        return includeKeywords.stream()
                .anyMatch(aboutJob1::contains);

    }
}
