import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.By.xpath;
import static org.openqa.selenium.Keys.ENTER;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class AvicTests {

    private WebDriver driver;

    @BeforeTest
    public void profileSetUp() {
        System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\chromedriver.exe");
    }

    @BeforeMethod
    public void testsSetUp() {
        driver = new ChromeDriver();//создаем экзаемпляр хром драйвера
       driver.manage().window().maximize();//открыли браузер на весь экран
        driver.get("https://avic.ua/");//открыли сайт
    }

    @Test(priority = 1)
    public void checkThatUrlContainsSearchWord() {
        driver.findElement(By.xpath("//input[@id='input_search']")).sendKeys("iPhone 11", Keys.ENTER);//вводим в поиск iPhone 11
        assertTrue(driver.getCurrentUrl().contains("query=iPhone"));//проверяем что урла содержит кверю
    }

    @Test(priority = 2)
    public void checkElementsAmountOnSearchPage() {
        driver.findElement(xpath("//input[@id='input_search']")).sendKeys("iPhone 11", ENTER);//вводим в поиск iPhone 11
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);//неявное ожидание 30 сек
        List<WebElement> elementList = driver.findElements(xpath("//div[@class='prod-cart__descr']"));//собрали элементы поиска в лист
        int actualElementsSize = elementList.size();//узнали количество элементов в листе
        assertEquals(actualElementsSize,12);//сравнили количество элементов актуальное с тем которое ожидаем
    }

    @Test(priority = 3)
    public void checkThatSearchResultsContainsSearchWord() {
        driver.findElement(xpath("//input[@id='input_search']")).sendKeys("iPhone 11", ENTER);//вводим в поиск iPhone 11
        List<WebElement> elementList = driver.findElements(xpath("//div[@class='prod-cart__descr']"));//собрали элементы поиска в лист
        for (WebElement webElement : elementList) { //прошлись циклом и проверили что каждый элемент листа содержит текс iPhone 11
            assertTrue(webElement.getText().contains("iPhone 11"));
        }
    }

    @Test(priority = 4)
    public void checkAddToCart() {
        driver.findElement(xpath("//span[@class='sidebar-item']")).click();//каталог товаров
        driver.findElement(xpath("//ul[contains(@class,'sidebar-list')]//a[contains(@href, 'apple-store')]")).click();//Apple Store
        driver.findElement(xpath("//div[@class='brand-box__title']/a[contains(@href,'iphone')]")).click();//iphone
        new WebDriverWait(driver, 30).until(
            webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));//wait for page loading
        WebDriverWait wait;
        wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(xpath("//a[@class='prod-cart__buy'][contains(@data-ecomm-cart,'Black (MWLT2)')]"))).click();//add to cart iphone
         wait = new WebDriverWait(driver, 30);//ждем пока не отобразится попап с товаром добавленным в корзину
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("js_cart")));
        driver.findElement(xpath("//div[@class='btns-cart-holder']//a[contains(@class,'btn--orange')]")).click();//продолжить покупки
        String actualProductsCountInCart =
            driver.findElement(xpath("//div[contains(@class,'header-bottom__cart')]//div[contains(@class,'cart_count')]")).getText();//получили 1 которя в карте (один продукт)
        assertEquals(actualProductsCountInCart, "1");
    }
    @Test(priority = 5)
    public void checkAllElementsOnThePageCanBuy(){
        Actions action = new Actions(driver);
        WebElement element = driver.findElement(xpath("//a[@class='sidebar-item'][text()='Электротранспорт']"));// з меню  вибрати електротранспорт
        action.moveToElement(element).perform();// навести на електротранспорт щоб зявилось наступне випадаюче менб
        driver.findElement(By.xpath("//a[@class='sidebar-item'][text()='Гироборды']")).click();//вибрати гіроборд
        driver.findElement(By.xpath("//label[text()='Только товар в наличии']")).click();//відфільтрувати тільки товар який є в наявності
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='filter__items filter__items--top checkbox']//a[@class='filter-tooltip js_filters_accept']"))).click();//клык тільки товар в наявності
        List<WebElement> elem = driver.findElements(By.xpath(("//div[@class='prod-cart height']")));
        for (WebElement webElement : elem) {
            assertTrue(webElement.getText().contains("Купить"));
        }
    }
    @Test(priority = 6)
    public void checkErrorAlertWithIncorrectData() {
        driver.get("https://avic.ua/sign-in");
        driver.findElement(xpath("//div[@class='sign-holder clearfix']//input[@name='login']")).sendKeys("login@gmail.com");
        driver.findElement(xpath("//input[@type='password']")).sendKeys("myPassword");
        driver.findElement(xpath("//button[@class='button-reset main-btn submit main-btn--green']")).click();
        assertTrue(driver.findElement(By.xpath("//div[@id='modalAlert']")).isDisplayed());
    }

    @Test(priority = 7)
    public void checkBasketTotal() {
    driver.findElement(xpath("//input[@id='input_search']")).sendKeys("Apple Watch",ENTER);
        WebDriverWait wait=new WebDriverWait(driver,30);
      wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='row js_more_content js_height-block']")));
    driver.findElement(By.xpath("//span[@class='prod-cart__article'][contains(text(),'Код товара: 47 220087')]")).click();
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@class='main-btn main-btn--green main-btn--large']"))).click();
    String sumTheSelectedProductInTheCArt= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='total-h']/span[@class='prise']"))).getText();
    String sumAllToPayInTheCart=driver.findElement(By.xpath("//div[@class='item-total']/span[@class='prise']")).getText();
    assertEquals(sumTheSelectedProductInTheCArt,sumAllToPayInTheCart);
    }

    @AfterMethod
    public void tearDown() {
        driver.close();
    }
}
