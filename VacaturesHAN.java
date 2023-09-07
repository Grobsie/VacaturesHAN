package mail;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class VacaturesHAN {

    public static void main(String[] args) {
        //webdriver part using Selenium project
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.han.nl/over-de-han/werken-bij-de-han/vacatures/#/");
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));  
        List<WebElement> vacatures = driver.findElements(By.className("finder-result-wrapper"));
        
        // create key:value pairs to store the title and URL of the corresponding job
        HashMap<String, String> vacaturesSaved = new HashMap<String, String>();
        HashMap<String, String> vacaturesCurrent = new HashMap<String, String>();
        HashMap<String, String> vacaturesNew = new HashMap<String, String>();
        
        //extract the title and URL from the WebElement to vacaturesCurrent
        for (WebElement i : vacatures) {
            vacaturesCurrent.put(i.getText().replace("\n", " | "),i.findElement(By.tagName("a")).getAttribute("href"));
        }
        driver.quit();
        
        //read the saved data from file to vacaturesSaved, if no file is found it just prints an error
        try {
            FileInputStream inStream = new FileInputStream("C:/hash.txt");
            ObjectInputStream ois = new ObjectInputStream(inStream);
            vacaturesSaved = (HashMap) ois.readObject();
            ois.close();
            inStream.close();
        } catch (Exception e) {
            System.out.println("something went wrong with reading the file");
        }
        
        //compare vacaturesSaved and vacaturesCurrent, if not already present, add to vacaturesNew.
        for (HashMap.Entry<String, String> entry : vacaturesCurrent.entrySet()) {
            if (vacaturesSaved.containsKey(entry.getKey())) {
                ;
            } else {
                vacaturesNew.put(entry.getKey(), entry.getValue());
            }
        }
        
        //overwrite the saved data with vacaturesCurrent
        try {
            FileOutputStream outStream = new FileOutputStream("C:/hash.txt");
            ObjectOutputStream oos = new ObjectOutputStream(outStream);
            oos.writeObject(vacaturesCurrent);
            oos.close();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //send report (TODO: create a single string and send it with mailer.java via gmail)
        if (vacaturesNew.size() == 1) {
            System.out.println("there is " + vacaturesNew.size() + " new job listing on the HAN website");
        } else {
            System.out.println("there are " + vacaturesNew.size() + " new job listings on the HAN website");
        }
        for (HashMap.Entry<String, String> entry : vacaturesNew.entrySet()) {
            System.out.println("--" + entry.toString());
        }       
    }
}
