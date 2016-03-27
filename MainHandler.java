package ServerHandler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

//database imports

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Servlet implementation class MainHandler
 */
@WebServlet("/ServerHandler/MainHandler")
public class MainHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MainHandler() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
		try
		{
			response.getWriter().println("I have been called from browser New !!!");
		}
		catch(Exception e)
		{
			System.out.println("Exception **** "+ e);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			
				System.out.println(request);
				String pageName = request.getParameter("page");
				if(pageName.equals("heat_map"))
				{
					HeatMapHandler heatMapHandler = new HeatMapHandler();
					heatMapHandler.getBins(request, response);
				}
					else if(pageName.equals("registration"))
					{
						RegistrationHandler registrationHandler = new RegistrationHandler();
						registrationHandler.registerUser(request, response);
					}
					else if(pageName.equals("report"))
					{
						ReportHandler reportHandler = new ReportHandler();
						reportHandler.reportLost(request, response);
					}
					else if(pageName.equals("login"))
					{
						LoginHandler loginHandler = new LoginHandler();
						loginHandler.loginUser(request, response);
					}
					} catch (Exception e) {
	        		// TODO Auto-generated catch block
	        		e.printStackTrace();
	        	}
			}
		}
