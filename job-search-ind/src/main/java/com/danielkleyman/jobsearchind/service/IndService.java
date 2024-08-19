package com.danielkleyman.jobsearchind.service;

import com.danielkleyman.jobsearchapi.service.WriteToExcel;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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
    private final String URL1 = "https://il.indeed.com/jobs?q=engineer&l=israel&fromage=1&vjk=1305588085fc99a8";
    private final String URL2 = "https://il.indeed.com/jobs?q=developer&l=israel&fromage=1&vjk=3ad75f0141d7c30a";
    private final String URL3 = "https://il.indeed.com/jobs?q=%D7%9E%D7%A4%D7%AA%D7%97&l=israel&fromage=1&vjk=810b903b668ad339";
    private final String URL4 = "https://il.indeed.com/jobs?q=%D7%9E%D7%AA%D7%9B%D7%A0%D7%AA&l=israel&fromage=1&vjk=0fc87a153af906f9";
    private final Map<String, List<String>> JOB_DETAILS = new LinkedHashMap<>();
    public static Set<String> alreadyAdded = new HashSet<>();
    private final List<String> listUrl = Arrays.asList(URL1, URL2, URL3, URL4);
    private final ExtractJobDetails extractJobDetails; // Injected dependency

    @Autowired
    public IndService(ExtractJobDetails extractJobDetails) {

        this.extractJobDetails = extractJobDetails; // Initialize injected dependency

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
        long startTime = System.currentTimeMillis();
        try {
            for (String url : listUrl) {
                openPage(driver, wait, url);
                //       scroller.start();
                //      Thread.sleep((long) (15 * 0.6 * 1000));
                //       scroller.stop();
                //      LOGGER.info("Scrolling stopped");
                extractJobDetails.extractJobDetails(driver, wait, JOB_DETAILS);
                WriteToExcel.writeToExcel(JOB_DETAILS, WEBSITE_NAME);
            }

            long endTime = System.currentTimeMillis();
            long totalTime = (endTime - startTime) / 1000;
            LOGGER.info("Extraction completed in " + totalTime + " seconds");
            LOGGER.info("Jobs parsed: " + JOB_DETAILS.size());
            JOB_DETAILS.clear();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
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

