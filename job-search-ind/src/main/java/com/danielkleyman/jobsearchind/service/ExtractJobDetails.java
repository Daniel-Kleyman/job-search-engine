package com.danielkleyman.jobsearchind.service;


import com.danielkleyman.jobsearchapi.service.AIService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
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
        String baseUrl = "https://www.indeed.com/m/basecamp/viewjob?viewtype=embedded&jk=";

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
                        List<String> details = new ArrayList<>();
                        String jobKey = jobNode.path("jobkey").asText();
                        String url = baseUrl + jobKey;
                        if (IndService.urlAlreadyAdded.contains(url)) {
                            continue;
                        }
                        // Extract and print job details
                        String jobTitle = jobNode.path("displayTitle").asText();
                        if (filterTitle(jobTitle)) {
                            System.out.println("Job title excluded: " + jobTitle);
                            continue;
                        }
                        System.out.println("Job Title: " + jobTitle);
                        details.add(jobTitle);
                        Thread.sleep(IndService.randomTimeoutCalculation(2000, 3000));
                        String jobDescription = getJobDescription(driver, wait, url);
                        Thread.sleep(IndService.randomTimeoutCalculation(2000, 3000));
                        if (!filterDescription(jobDescription)) {
                            System.out.println("Job description excluded for job title: " + jobTitle);
                            continue;
                        }
                        int aiResponse = aiService.getResponse(jobDescription);
                        System.out.println(details.get(0) + " gpt score = " + aiResponse);
                        if (aiResponse < 21) {
                            continue; // Skip this job card if the extended text does not match filter criteria
                        }
                        details.add(jobDescription);
                        String companyName = jobNode.path("company").asText();
                        details.add(companyName);
                        String city = jobNode.path("jobLocationCity").asText();
                        details.add(city);
                        jobDetails.putIfAbsent(url, details);
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


    private String getJobDescription(WebDriver driver, WebDriverWait wait, String url) {
        String jobDescriptionText = "";
        driver.get(url);

        // Wait for the page to load and the _initialData to be present
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));

        // Get the page source
        String pageSource = driver.getPageSource();

        // Define the regex pattern to find the embedded JSON data
        String regexPattern = "_initialData=(\\{.+?\\});";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(pageSource);
        String jsonString = "";

        if (matcher.find()) {
            jsonString = matcher.group(1);

            // Print the entire JSON data for inspection
            //    System.out.println("Extracted JSON Data:");
            //    System.out.println(jsonString);

            // Parse the JSON data
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode rootNode = objectMapper.readTree(jsonString);

                // Navigate to the desired field
                JsonNode jobDataNode = rootNode.path("hostQueryExecutionResult").path("data").path("jobData").path("results").get(0).path("job").path("description");
                jobDescriptionText = jobDataNode.path("text").asText();

               return jobDescriptionText;

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No embedded data found.");
        }
        return jobDescriptionText;
    }

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
                "penetration", " investigations", "intelligence", "hrbp", "officer", "curriculum", " business", "team", "staff", "automation", "machine learning"
                , "mechanic", "ראש", "sr", "server");

        // Check if any exclude keyword is present in the job title
        return excludeKeywords.stream()
                .anyMatch(jobTitle.toLowerCase()::contains);
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
