package com.b2gsoft.mrb.Utils;

/**
 * Created by Nahid on 16/9/2019.
 */
public class Url {

    public static String BASE_URL = "BASE_URL";
    public static String DEFAULT = "BASE_URL/api/";

    public static String LOGIN = DEFAULT+ "UserLogin";

    public static String Expense = DEFAULT+ "daily-expense?token=";

    public static String ExpenseUpdate = DEFAULT+ "daily-expense/";

    public static String ExpenseHistory = DEFAULT+ "daily-expense?token=";

    public static String DailyExpenseHistory = DEFAULT+ "daily-expense?date=";

    public static String YearlyExpenseHistory = DEFAULT+ "daily-expense?this_year&in_single_field&token=";

    public static String MonthlyExpenseHistory = DEFAULT+ "daily-expense?this_month&in_single_field&token=";

    public static String DueExpenseHistory = DEFAULT+ "daily-expense-due-list?&token=";

    public static String DueExpenseSubmit = DEFAULT+ "daily-expense-due-into-null/";

    public static String DueIncomeHistory = DEFAULT +"daily-income-due-list?token=";

    public static String DueIncomeSubmit = DEFAULT +"daily-income-due-into-null/";


    public static String ExpenseWiseHistory = DEFAULT+ "daily-expense?expense_sector_id=";

    public static String SubmitIncome = DEFAULT +"daily-income?token=";
    public static String IncomeUpdate = DEFAULT +"daily-income/";

    public static String GetDailyIncomeHistory = DEFAULT +"daily-income?token=";

    //public static String DailyIncomeHistory = DEFAULT +"daily-income?today&token=";
    public static String DailyIncomeHistory = DEFAULT +"daily-income?date=";
    public static String DailyIncomeCustomerWise = DEFAULT +"daily-income?customer_id=";

    public static String GetPot = DEFAULT+"pot?mill=";

    public static String PotSave = DEFAULT+"pot?token=";
    public static String PotUpdate = DEFAULT+"pot/";

    public static String WeeklyBillSave = DEFAULT + "week-expense-sector?token=";

    public static String DashBoardData = DEFAULT +"dashboard-data?token=";

    public static String MonthlyIncomeHistory = DEFAULT+ "daily-income?this_month&in_single_field&token=";

    public static String YearlyIncomeHistory = DEFAULT+ "daily-income?this_year&in_single_field&token=";

    public static String KhorakiSave = DEFAULT + "khoraki?token=";
    public static String CreateUser = DEFAULT + "user?token=";
    public static String DeleteUser = DEFAULT + "user/";

    public static String Contracts = DEFAULT + "contract?token=";
    public static String Vault = DEFAULT + "personal-vault?token=";
    public static String Reminder = DEFAULT +"reminder?token=";
    public static String Customer = DEFAULT +"customer";
    public static String SubmitAdvance = DEFAULT + "advance";
    public static String SubmitAdvanceDEV = DEFAULT + "advance-module-dev";

    public static String SetPermission = DEFAULT + "user/permissions?token=";
    public static String ManagerList = DEFAULT + "user/managers?";
    public static String UserList = DEFAULT + "members?";

    public static String ExpenseCategories = DEFAULT + "expense-sector?token=";
    public static String ExpenseCategoryDelete = DEFAULT + "expense-sector/";
    public static String ExpenseSubSector = DEFAULT + "expense-sub-sector/";
    public static String IncomeCategories = DEFAULT + "income-class?token=";
    public static String IncomeCategoryDelete = DEFAULT + "income-class/";

    public static String DueDetails = DEFAULT + "payment?token=";
    public static String AdvanceDetails = DEFAULT + "advance-details?token=";

    public static String PotDetails = DEFAULT + "pot/details?token=";

    public static String Round = DEFAULT + "round";

    public static String SummeryReport = DEFAULT + "summary?token=";
    public static String Report = DEFAULT + "report?token=";

    public static String SummeryReportGenerate = BASE_URL + "summary/print";
    public static String SummeryReportDownload = BASE_URL + "summary.pdf";

    public static String ReportGenerate = BASE_URL + "report/print";
    public static String ReportDownload = BASE_URL + "report.pdf";

    public static String Load = DEFAULT + "load?token=";

    public static String MonthlyIncome = DEFAULT + "daily-income-monthly?token=";
    public static String YearlyIncome = DEFAULT + "daily-income-yearly?token=";
    public static String MonthlyExpense = DEFAULT + "daily-expense-monthly?token=";
    public static String YearlyExpense = DEFAULT + "daily-expense-yearly?token=";
}
