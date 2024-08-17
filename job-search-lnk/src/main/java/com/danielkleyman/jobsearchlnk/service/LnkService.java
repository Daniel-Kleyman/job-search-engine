package com.danielkleyman.jobsearchlnk.service;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class LnkService {

    private static final long SCHEDULED_TIME = 900000;
    private static final String SCHEDULED_TIME_STRING = "900";
    public static final Logger LOGGER = Logger.getLogger(LnkService.class.getName());
    public static final String CHROME_DRIVER_PATH = System.getenv("CHROME_DRIVER_PATH");
    private static final String WEBSITE_NAME = "Linkedin";
    private final String firstUrl = String.format("https://www.linkedin.com/jobs/search?keywords=&location=Israel&geoId=101620260&f_TPR=r%s&position=1&pageNum=0", SCHEDULED_TIME_STRING);
    private final Map<String, List<String>> jobDetails = new LinkedHashMap<>();

    public static int jobCount;
    private final ExtractJobDetails extractJobDetails; // Injected dependency

    @Autowired
    public LnkService(ExtractJobDetails extractJobDetails) {

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
        Scrolling scroller = new Scrolling(driver);
        long startTime = System.currentTimeMillis();
        try {
            openPage(driver, wait);
            printJobCount(driver);
            scroller.start();
            Thread.sleep((long) (jobCount * 0.6 * 1000));
            scroller.stop();
            LOGGER.info("Scrolling stopped");
            extractJobDetails.extractJobDetails(driver, wait, jobDetails);
            WriteToExcel.writeToExcel(jobDetails, WEBSITE_NAME);
            long endTime = System.currentTimeMillis();
            long totalTime = (endTime - startTime) / 1000;
            LOGGER.info("Extraction completed in " + totalTime + " seconds");
            LOGGER.info("Jobs parsed: " + jobDetails.size());
            jobDetails.clear();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    private void openPage(WebDriver driver, WebDriverWait wait) {
        boolean reload = true;
        while (reload) {
            driver.get(firstUrl);
            try {
                WebElement jobCounter = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".results-context-header__job-count")));

                if (jobCounter != null) {
                    LOGGER.info("proceeding");
                    reload = false;
                }

            } catch (TimeoutException e) {
                try {
                    WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"main-content\"]/section[1]/h1/strong")));
                    try {
                        LOGGER.info("waiting time before reloading page");
                        Thread.sleep(900000);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        LOGGER.severe("Interrupted while waiting: " + ex.getMessage());
                    }
                } catch (TimeoutException ignored) {
                }
                LOGGER.info("reloading page");
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                LOGGER.severe("Interrupted while waiting: " + ex.getMessage());
            }
        }
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

    private void printJobCount(WebDriver driver) {
        WebElement jobCountElement = driver.findElement(By.cssSelector(".results-context-header__job-count"));
        String jobCountText = jobCountElement.getText().replace("+", "").replace(",", "");

        try {
            jobCount = Integer.parseInt(jobCountText);
        } catch (NumberFormatException e) {
            LOGGER.severe("Failed to parse job count: " + e.getMessage());
            return;
        }
        LOGGER.info("Job count: " + jobCount);
    }
}

