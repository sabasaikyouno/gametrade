import org.openqa.selenium.{By, Cookie, JavascriptExecutor, Keys}
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.interactions.Actions

import java.io.File
import java.time.Duration
import scala.jdk.CollectionConverters._

object Main {
  def main(args: Array[String]): Unit = {
    val options = new ChromeOptions()
    options.addArguments("--headless=new")
    options.addArguments("--disable-gpu")
    options.addArguments("--window-size=945,1020")
    options.addExtensions(new File("src\\main\\resources\\Buster Captcha Solver for Humans 2.0.1.0.crx"))
    options.addExtensions(new File("src\\main\\resources\\Page Ruler 0.1.4.0.crx"))

    val chrome: ChromeDriver = new ChromeDriver(options)
    chrome.manage().timeouts().implicitlyWait(Duration.ofSeconds(5))

    //login
    chrome.get("https://gametrade.jp/")
    chrome.manage().addCookie(new Cookie("remember_token", "688cdba93e606c4681be7902dee4291b7d258472"))

    while (true) {
      for (editUrlList <- getSellList(chrome)) {
        for(editUrl <- editUrlList) {
          try {
            chrome.get(editUrl)
            //テキスト編集
            val textArea = chrome.findElement(By.className("exhibits_description"))
            textArea.sendKeys(if (textArea.getText.last == ' ') Keys.BACK_SPACE else Keys.SPACE)
            Thread.sleep(2000)

            //承認クリック
            chrome.findElement(By.name("agreement")).click()
            Thread.sleep(1000)

            //送信クリック
            chrome.findElements(By.cssSelector("button[style='display: block; margin: 30px auto; width: 260px; padding: 10px; background-color: #cc2310; color: white;']"))
              .getLast
              .click()

            Thread.sleep(2000)

            //reCAPTCHA
            if (chrome.getCurrentUrl == editUrl) {
              val actions = new Actions(chrome)
              actions.moveToLocation(381, 553).click().perform()
              Thread.sleep(5000)
              actions.moveToLocation(439, 253).click().perform()
            }

            Thread.sleep(2000)
          } catch {
            case l => println(l)
          }
        }

        Thread.sleep(1800000)
      }
    }
  }

  //出品一覧
  private def getSellList(chrome: ChromeDriver) = {
    chrome.get("https://gametrade.jp/mypage/sell_history")

    chrome
      .findElement(By.className("mypage_exhibits_list"))
      .findElements(By.tagName("a"))
      .asScala
      .map(ele => makeEditUrl(ele.getAttribute("href")))
      .toList
      .grouped(5)
      .toList
  }

  private def makeEditUrl(url: String) = {
    val tmp = url.drop(21)

    url.take(20) + tmp.dropWhile(_ != '/') + "/edit"
  }
}
