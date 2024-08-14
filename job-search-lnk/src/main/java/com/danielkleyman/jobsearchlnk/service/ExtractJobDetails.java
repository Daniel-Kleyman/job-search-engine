package com.danielkleyman.jobsearchlnk.service;


import com.danielkleyman.jobsearchcommon.gpt.AIService;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

@Component
public class ExtractJobDetails {
    private final AIService aiService;
    int jobsVisibleOnPage;

    public ExtractJobDetails() {
        this.aiService = new AIService();
        this.jobsVisibleOnPage = 0;
    }

    public void extractJobDetails(WebDriver driver, WebDriverWait wait, Map<String, List<String>> jobDetails) {

        extractProcess(driver, wait, jobDetails);

    }

    private void extractProcess(WebDriver driver, WebDriverWait wait, Map<String, List<String>> jobDetails) {

        System.out.println("Waiting for page to load...");

        try {
            System.out.println("Searching for job container");
            // Wait until the job container is visible
            WebElement jobContainer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ul[contains(@class, 'jobs-search__results-list')]")));
            System.out.println("Job container is found");
            // Find all job cards on the page
            List<WebElement> jobCards = driver.findElements(By.xpath("//div[contains(@class, 'job-search-card')]"));
            if (jobCards.isEmpty()) {
                System.err.println("No job cards found.");
                return;
            }
            System.out.println("Job cards are found");
            jobCardsParsing(driver, wait, jobDetails, jobCards);

        } catch (TimeoutException e) {
            System.err.println("Timeout while waiting for job container: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
        System.out.println("Jobs visible: " + jobsVisibleOnPage);
    }

    private void jobCardsParsing(WebDriver driver, WebDriverWait wait, Map<String, List<String>> jobDetails, List<WebElement> jobCards) {
        for (int i = 0; i < jobCards.size(); i++) {
            jobsVisibleOnPage++;
            WebElement jobCard = jobCards.get(i);
            System.out.println("get card number: " + i);
            String url = "";
            List<String> details = new ArrayList<>();
            try {
                // Extract job title and URL
                boolean extractionTitleAndUrl = extractTitleAndUrl(jobCard, url, details);
                if (!extractionTitleAndUrl) {
                    continue; // Skip to the next job card if extraction failed
                }
                showExpandedContent(wait, i, jobCard, jobCards);
                boolean extractionExpandedContent = extractExpandedContent(driver, details);
                if (!extractionExpandedContent) {
                    continue; // Skip to the next job card if extraction failed
                }

                // Extract company name
                extractCompanyName(details, wait);
                // Extract city
                extractCity(details, wait);
                // Add job details to the map
                jobDetails.putIfAbsent(url, details);

            } catch (Exception e) {
                System.err.println("Unexpected error extracting details from job card: " + e.getMessage());
            }
        }
    }

    private void extractCity(List<String> details, WebDriverWait wait) {
        try {
            WebElement cityElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span.topcard__flavor.topcard__flavor--bullet")));
            String city = cityElement.getText().trim();
            details.add(city);
        } catch (NoSuchElementException | TimeoutException e) {
            System.err.println("City element not found: " + e.getMessage());
            details.add("City not available");
        }
    }

    private void extractCompanyName(List<String> details, WebDriverWait wait) {
        try {
            WebElement companyElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a.topcard__org-name-link")));
            String companyName = companyElement.getText();
            details.add(companyName);
        } catch (NoSuchElementException | TimeoutException e) {
            System.err.println("Company name element not found: " + e.getMessage());
            details.add("Company name not available");
        }
    }

    private boolean extractExpandedContent(WebDriver driver, List<String> details) {
        // Use a shorter wait for the expanded content
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(1));
        WebElement expandedContent = null;
        try {
            expandedContent = shortWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.show-more-less-html__markup")));
            String extendedText = expandedContent.getText();
            if (!filterDescription(extendedText)) {
                System.out.println("Extended text excluded for job: " + details.get(0));
                return false; // Skip this job card if the extended text does not match filter criteria
            }
            int aiResponse = aiService.getResponse(extendedText);
            System.out.println(" " + details.get(0) + " gpt score = " + aiResponse);
            if (aiResponse < 21) {
                //   System.out.println("text for job title:  " + details.get(0) + " excluded by ai = " + aiResponse);
                return false; // Skip this job card if the extended text does not match filter criteria
            }
            details.add(extendedText);
            //                    System.out.println("Extended text added: " + extendedText);
        } catch (TimeoutException e) {
            System.err.println("Extended content not found within 1 second.");
            details.add("Extended text not available");
        }
        return true;
    }

    private void showExpandedContent(WebDriverWait wait, int i, WebElement jobCard, List<WebElement> jobCards) {
        boolean showMoreVisible = false;
        while (!showMoreVisible) {
            // Click on the job card to expand it
            jobCard.click();
            try {
                // Try to find and click the "Show more" button
                WebElement showMoreButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button.show-more-less-html__button")));
                showMoreButton.click();
                showMoreVisible = true; // Exit loop if the button was clicked
                System.out.println("Clicked 'Show more' button.");
            } catch (NoSuchElementException | TimeoutException e) {
                // If the "Show more" button is not found, collapse the previous job card if possible
                if (i > 0) {
                    WebElement previousJobCard = jobCards.get(i - 1);
                    previousJobCard.click(); // Collapse the previous job card
                    System.out.println("Collapsed previous job card.");
                }
                // Retry the current job card by clicking it again
                jobCard.click();
            }
        }
    }

    private boolean extractTitleAndUrl(WebElement jobCard, String url, List<String> details) {
        String title = "";
        try {
            WebElement titleElement = jobCard.findElement(By.xpath(".//h3[contains(@class, 'base-search-card__title')]"));
            title = titleElement.getText();
            if (!filterTitle(title.toLowerCase())) {
                System.out.println("Job title excluded: " + title);
                return false; // Skip this job card if title matches filter criteria
            }
            details.add(title);

            WebElement urlElement = jobCard.findElement(By.cssSelector("a.base-card__full-link"));
            url = urlElement.getAttribute("href");

        } catch (NoSuchElementException e) {
            System.err.println("Job title or URL element not found in a job card.");
            return false; // Skip this job card if title or URL is missing
        }
        return true;
    }


    private static boolean filterDetails(List<String> details) {
        Set<String> excludeKeywords = Set.of("senior", "lead", "leader", "devops", "manager", "qa", "mechanical", "infrastructure", "integration", "civil",
                "principal", "customer", "embedded", "system", " verification", "electrical", "support", "complaint", "solution", "solutions", "simulation", "technical",
                "manufacturing", "validation", "finops", "hardware", "devsecops", "motion", "machine Learning", "design", "sr.", "quality", "architect", "head",
                "director", "president", "executive", "detection", "industrial", "chief", "specialist", "algorithm", "architecture", "admin", " researcher",
                " data science", "webmaster", "medical", "associate", "mrb", "accountant", "waiter", "dft", "test", "musicologist", "sales", "media", "product",
                "reliability", "account", "representative", "Architect", "Analyst", "Account", "Executive", "Specialist", "Associate", "devtest", "big data", "digital",
                "coordinator", "intern", "researcher", "network", "security", "malware", " intelligence", " algo-dev", "electro-optics", "secops", "implementer",
                "ml", "picker", "revenue", "controller", "פלנר", "טכנאי");
        // Convert the job title to lower case for case-insensitive comparison
        String jobTitle = details.get(0).toLowerCase();
        String aboutJob = details.get(3).toLowerCase();
        // Exclude entries if the job title contains any of the excludeKeywords
        boolean shouldExclude = excludeKeywords.stream()
                .anyMatch(keyword -> jobTitle.contains(keyword));
        // Include only entries that contain at least one of the includeKeywords

        boolean shouldAlsoInclude = aboutJob.contains("java");

        return !shouldExclude && shouldAlsoInclude;
    }

    private static boolean filterTitle(String jobTitle) {
        Set<String> excludeKeywords = Set.of(
                "senior", "lead", "leader", "devops", "manager", "qa", "mechanical", "infrastructure", "integration", "civil",
                "principal", "customer", "embedded", "system", "verification", "electrical", "support", "complaint", "solution", "solutions",
                "simulation", "technical", "manufacturing", "validation", "finops", "hardware", "devsecops", "motion", "machine learning",
                "design", "sr", "quality", "staff", "compliance", "administrator", "marketing", "director", "bookkeeper", "inspector", "nurse",
                "training", "expert");
        Set<String> includeKeywords = Set.of(
                "developer", "engineer", "programmer", "backend", "back-end", "back end", "fullstack", "full-stack", "full stack",
                "software", "fs", "java", "מתחנת", "מפתח"
        );

        // Check if any exclude keyword is present in the job title
        boolean shouldExclude = excludeKeywords.stream()
                .anyMatch(keyword -> jobTitle.contains(keyword));

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
        boolean shouldInclude = includeKeywords.stream()
                .anyMatch(keyword -> aboutJob1.contains(keyword));

        return shouldInclude;

    }
}
