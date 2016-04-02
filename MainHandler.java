package ServerHandler;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Class Name : MainHandler
 * Purpose : To receive user's request and send the request to corresponding controllers
 */
@WebServlet("/MainHandler")
public class MainHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public MainHandler() {
        super();
    }

    /**
     * Method Name : doGet
     * Purpose : To verify if the server is running from browser
     * Parameters : HttpServletRequest request, HttpServletResponse response
     * Return value : Null
     */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{
			response.getWriter().println("MainHandler: I have been called from browser!!!!!");
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Exception in doGet in MainHandler: "+ e);
		}
	}

    /**
     * Method Name : doPost
     * Purpose : Categorize the request and invoke corresponding function from the related controller 
     * Parameters : HttpServletRequest request, HttpServletResponse response
     * Return value : Null
     */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{			
			String pageName = request.getParameter("page");
			if(pageName.equals("heat_map")){
				HeatMapHandler.getBins(request, response);
			}
			else if(pageName.equals("registration")){
				RegistrationHandler.registerUser(request, response);
			}
			else if(pageName.equals("report")){
				ReportHandler.reportLost(request, response);
			}
			else if(pageName.equals("login")){
				LoginHandler.loginUser(request, response);
			}
		}
		catch(Exception e){
    		e.printStackTrace();
    		System.out.println("Exception in doPost in MainHandler: "+ e);
        }
	}
}
