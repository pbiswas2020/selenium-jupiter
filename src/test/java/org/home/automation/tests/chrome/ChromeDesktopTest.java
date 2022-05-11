package org.home.automation.tests.chrome;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeUnit;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.common.base.Verify;

import io.github.bonigarcia.seljup.Arguments;
import io.github.bonigarcia.seljup.SeleniumJupiter;
import io.github.bonigarcia.seljup.SingleSession;

@ExtendWith(SeleniumJupiter.class)
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application.properties")
@TestMethodOrder(OrderAnnotation.class)
@SingleSession

public class ChromeDesktopTest {
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().getClass());
	@Value("${test.base.url}")
	private String testBaseUrl;

	@BeforeAll
	static void setup() {
		System.setProperty("sel.jup.recording", "true");
		System.setProperty("sel.jup.screenshot.at.the.end.of.tests", "true");
		System.setProperty("sel.jup.screenshot.format", "png");
		System.setProperty("sel.jup.output.folder", "target/screenshots");
	}

	@Test
	@Order(1)
	void testTitle(@Arguments("--headless") ChromeDriver driver) {
		driver.get(testBaseUrl);
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		logger.info(driver.getTitle());
		assertNotNull(driver.getTitle());
		assertThat(driver.getTitle(), containsString("The Internet"));
		logger.info("Title OK");
	}

	@Test
	@Order(2)
	void testLoginWithValidUserIdPassword(ChromeDriver driver) {

		driver.get(testBaseUrl + "/login");
		driver.findElementById("username").click();
		driver.findElementById("username").sendKeys("tomsmith");
		driver.findElementById("password").click();
		driver.findElementById("password").sendKeys("SuperSecretPassword!");
		// //*[@id='login']/button/i
		driver.findElementByXPath("//*[@id=\"login\"]/button/i").click();

		logger.info("After click login button : " + driver.getCurrentUrl());
//		assertThat(driver.getCurrentUrl(), containsString(testBaseUrl.concat("/secure")));

		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		driver.findElementByXPath("//*[@id=\"content\"]/div/a/i").click();
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		Verify.verify(driver.getCurrentUrl().equals(testBaseUrl.concat("/login")), "Error: %s, %s",
				driver.getCurrentUrl(), testBaseUrl.concat("/secure"));

		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.close();
	}

	@Test
	@Order(3)

	void testValidLoginIdWithInvalidPassword(ChromeDriver driver) {

		driver.get(testBaseUrl + "/login");
		driver.findElementById("username").click();
		driver.findElementById("username").sendKeys("tomsmith");
		driver.findElementById("password").click();
		driver.findElementById("password").sendKeys("SuperSecretPassword");
		driver.findElementByXPath("//*[@id=\"login\"]/button/i").click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
//		assertThat(driver.getCurrentUrl(), containsString(testBaseUrl.concat("/secure")));
		String actualResult = driver.getCurrentUrl();
		String expectedResult = testBaseUrl.concat("/secure");
		Verify.verify(actualResult.equals(expectedResult), "Error: %s, %s", actualResult, expectedResult);

//		driver.close();

	}

}
