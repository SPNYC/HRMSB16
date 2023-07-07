package steps;

import org.junit.Assert;
import utils.CommonMethods;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import utils.Constants;
import utils.ExcelReader;

import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class AddEmployeeSteps extends CommonMethods {


        @When("user clicks on PIM option")
        public void user_clicks_on_pim_option() {
            //  WebElement pimOption = driver.findElement(By.id("menu_pim_viewPimModule"));
            //   pimOption.click();
            click(dashboardPage.pimOption);
        }

        @When("user clicks on add employee button")
        public void user_clicks_on_add_employee_button() {
            //   WebElement addEmployeeBUtton = driver.findElement(By.id("menu_pim_addEmployee"));
            // addEmployeeBUtton.click();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            click(dashboardPage.addEmployeeButton);
        }

        @When("user enters firstname and lastname")
        public void user_enters_firstname_and_lastname() {
             WebElement firstNameTextField = driver.findElement(By.id("firstName"));
            firstNameTextField.sendKeys("aendro");
            //sendText("aendro", addEmployeePage.firstNameField);

            WebElement lastNameTextField = driver.findElement(By.id("lastName"));
            lastNameTextField.sendKeys("farewell");
            //sendText("farewell", addEmployeePage.lastNameField);
        }

        @When("user clicks on save button")
        public void user_clicks_on_save_button() {
            //   WebElement saveButton = driver.findElement(By.id("btnSave"));
            // saveButton.click();
            click(addEmployeePage.saveButton);
        }

        @Then("employee added successfully")
        public void employee_added_successfully() {
            System.out.println("Employee added successfully");
        }
    @When("user enters {string} and {string} and {string}")
    public void user_enters_and_and(String firstName, String middleName, String lastName) {
       sendText(firstName, addEmployeePage.firstNameField);
       sendText(middleName, addEmployeePage.middleNameField);
       sendText(lastName, addEmployeePage.lastNameField);
    }

    @When("user enters {string} and {string} and {string} in data driven format")
    public void user_enters_and_and_in_data_driven_format(String firstName, String middleName, String lastName) {
        sendText(firstName, addEmployeePage.firstNameField);
        sendText(middleName, addEmployeePage.middleNameField);
        sendText(lastName, addEmployeePage.lastNameField);
    }

    @When("user enters firstname and middlename and lastname and verify employee has added")
    public void user_enters_firstname_and_middlename_and_lastname_and_verify_employee_has_added
            (io.cucumber.datatable.DataTable dataTable) {

    //we need list of maps to get multiple value from datatable which is coming
        //from feature file
        List<Map<String, String>> employeeNames=dataTable.asMaps();

        for(Map<String,String> employee:employeeNames){
            //getting the value against the key in map
            String firstNameValue=employee.get("firstName");
            String middleNameValue=employee.get("middleName");
            String lastNameValue=employee.get("lastName");

            //filling the name in the fields
            sendText(firstNameValue,addEmployeePage.firstNameField);
            sendText(middleNameValue, addEmployeePage.middleNameField);
            sendText(lastNameValue, addEmployeePage.lastNameField);
            click(addEmployeePage.saveButton);

            //often adding one employee, we will add another employee
            //for this, we are clicking on add employee button in the loop itself
            click(dashboardPage.addEmployeeButton);
        }
    }

    @When("user adds multiple employees using excel from {string} and verify it")
    public void user_adds_multiple_employees_using_excel_from_and_verify_it(String sheetName) throws InterruptedException {
            //here we are getting the data from excel file using parameters
       List<Map<String,String>> newEmployees= ExcelReader.read(sheetName, Constants.EXCEL_READER_PATH);
        Iterator<Map<String,String>> itr= newEmployees.iterator();

        //it will check whether we have  new element/value or not
        while(itr.hasNext()){
            //in this map we have data from single employee one by one it will give
           Map<String,String> mapNewEmp= itr.next();

           //keys what we are passing here should match with the key in excel
           sendText(mapNewEmp.get("firstName"), addEmployeePage.firstNameField);
           sendText(mapNewEmp.get("lastName"), addEmployeePage.lastNameField);
           sendText(mapNewEmp.get("middleName"), addEmployeePage.middleNameField);
           sendText(mapNewEmp.get("photograph"), addEmployeePage.photograph);

           //we can enter username and password only after selecting the checkbox

            if(!addEmployeePage.checkBoxLocator.isSelected()){
                click(addEmployeePage.checkBoxLocator);
            }
            sendText(mapNewEmp.get("username"), addEmployeePage.usernameTextFieldBox);
            sendText(mapNewEmp.get("password"), addEmployeePage.passwordTextFieldBox);
            sendText(mapNewEmp.get("confirmPassword"), addEmployeePage.confirmPasswordBox );

            //here we are fetching the employee id from the UI using get attribute method
           String empIdValue= addEmployeePage.employeeIdField.getAttribute("value");

            click(addEmployeePage.saveButton);
            //we have to verify that the employee has been added
            Thread.sleep(3000);
            click(dashboardPage.empListOption);
            //searching the employee using emp id which we just got
            sendText(empIdValue,employeeSearchPage.idTextField);

            //print the value from the table row
            List<WebElement> rowData = driver.findElements(By.xpath("//table[@id='resultTable']/tbody/tr"));
            for(int i=0; i< rowData.size();i++){
                System.out.println("I am inside the loop");
             //it will return one by one ll the data from the row
              String rowText=  rowData.get(i).getText();
              //it will print the complete row data
                //output of this will be empid firstname middlename lastname
                System.out.println(rowText);

                //we have to verify this data against the data coming from excel
                String expectedData=empIdValue+" "+mapNewEmp.get("firstName")+" "+mapNewEmp.get("middleName")+" "+
                        mapNewEmp.get("lastName");

                Assert.assertEquals(rowText,expectedData);
                //second option to use
               // Assert.assertTrue(expectedData.equals(rowText));
            }
            //to add more employee we need to click on add employee button
            click(dashboardPage.addEmployeeButton);
        }


    }

}

