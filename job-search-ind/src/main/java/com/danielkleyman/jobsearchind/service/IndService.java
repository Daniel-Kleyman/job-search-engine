package com.danielkleyman.jobsearchind.service;

import com.danielkleyman.jobsearchapi.service.WriteToExcel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class IndService {

    private static final long SCHEDULED_TIME = 7200000;
    public static final Logger LOGGER = Logger.getLogger(IndService.class.getName());
    public static final String CHROME_DRIVER_PATH = System.getenv("CHROME_DRIVER_PATH");
    private static final String WEBSITE_NAME = "Indeed";
    private final String URL = "https://il.indeed.com/jobs?q=&l=israel&fromage=1&vjk=087662bdbb912e05";
//    private final String URL1 = "https://il.indeed.com/jobs?q=engineer&l=israel&fromage=1&vjk=1305588085fc99a8";
//    private final String URL2 = "https://il.indeed.com/jobs?q=developer&l=israel&fromage=1&vjk=3ad75f0141d7c30a";
//    private final String URL3 = "https://il.indeed.com/jobs?q=%D7%9E%D7%A4%D7%AA%D7%97&l=israel&fromage=1&vjk=810b903b668ad339";
//    private final String URL4 = "https://il.indeed.com/jobs?q=%D7%9E%D7%AA%D7%9B%D7%A0%D7%AA&l=israel&fromage=1&vjk=0fc87a153af906f9";
    private final Map<String, List<String>> JOB_DETAILS = new LinkedHashMap<>();
    private final ExtractJobDetails extractJobDetails; // Injected dependency
    public static List<String> urlAlreadyAdded = new ArrayList<>();
    public static int jobCount;

    @Autowired
    public IndService(ExtractJobDetails extractJobDetails) {

        this.extractJobDetails = extractJobDetails; // Initialize injected dependency
        jobCount = 0;
    }

    @Scheduled(fixedRate = SCHEDULED_TIME)
    public void scheduledGetResults() {
        WebDriver localDriver = null;
        WebDriverWait localWait;
        try {
            localDriver = initializeWebDriver();
            localWait = new WebDriverWait(localDriver, Duration.ofSeconds(10));
            getResults(localDriver, localWait);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during scheduled task", e);
        } finally {
            if (localDriver != null) {
                localDriver.quit();
            }
        }
    }

    public void getResults(WebDriver driver, WebDriverWait wait) {
        // Scrolling scroller = new Scrolling(driver);
        jobCount = 0;
        long startTime = System.currentTimeMillis();
        openPage(driver, wait, URL);
        try {
            while (isNextPageButtonVisible(driver, wait)) {
                Thread.sleep(randomTimeoutCalculation(4000, 8000));
                extractJobDetails.extractJobDetails(driver, wait, JOB_DETAILS);
                Thread.sleep(randomTimeoutCalculation(4000, 8000));
                LOGGER.info("Jobs found: " + jobCount);
                clickNextPage(driver, wait);
            }
            WriteToExcel.writeToExcel(JOB_DETAILS, WEBSITE_NAME);
            long endTime = System.currentTimeMillis();
            long totalTime = (endTime - startTime) / 1000;
            LOGGER.info("Extraction completed in " + totalTime + " seconds");
            LOGGER.info("Jobs found: " + jobCount);
            LOGGER.info("Jobs parsed: " + JOB_DETAILS.size());
            JOB_DETAILS.clear();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    private boolean isNextPageButtonVisible(WebDriver driver, WebDriverWait wait) {
        try {
            WebElement nextPageButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a[data-testid='pagination-page-next']")));
            return nextPageButton.isDisplayed();
        } catch (Exception e) {
            return false;  // If the element is not found or not visible
        }
    }

    public void clickNextPage(WebDriver driver, WebDriverWait wait) {
        try {
            // Wait until the "Next Page" button is clickable
            WebElement nextPageButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[data-testid='pagination-page-next']")));

            // Click on the "Next Page" button
            nextPageButton.click();

            System.out.println("Navigated to the next page.");
        } catch (Exception e) {
            System.err.println("Failed to click on the Next Page button: " + e.getMessage());
        }
    }

    public static long randomTimeoutCalculation(long min, long max) {
        Random random = new Random();
        // Generate a random long value between 3000 and 6000 milliseconds
        return min + random.nextInt((int) (max - min + 1));
    }

    private void openPage(WebDriver driver, WebDriverWait wait, String url) {
        boolean reload = true;
        //      while (reload) {
        driver.get(url);
//            try {
//                WebElement jobCounter = wait.until(ExpectedConditions.visibilityOfElementLocated(
//                        By.cssSelector(".results-context-header__job-count")));
//
//                if (jobCounter != null) {
//                    LOGGER.info("proceeding");
//                    reload = false;
//                }
//
//            } catch (TimeoutException e) {
//                try {
//                    WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"main-content\"]/section[1]/h1/strong")));
//                    try {
//                        LOGGER.info("waiting time before reloading page");
//                        Thread.sleep(900000);
//                    } catch (InterruptedException ex) {
//                        Thread.currentThread().interrupt();
//                        LOGGER.severe("Interrupted while waiting: " + ex.getMessage());
//                    }
//                } catch (TimeoutException ignored) {
//                }
//                LOGGER.info("reloading page");
//            }
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException ex) {
//                Thread.currentThread().interrupt();
//                LOGGER.severe("Interrupted while waiting: " + ex.getMessage());
//            }
//        }
    }

    private WebDriver initializeWebDriver() {
        if (CHROME_DRIVER_PATH == null) {
            throw new IllegalStateException("CHROME_DRIVER_PATH environment variable not set");
        }

        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        // options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        return driver;
    }
}

