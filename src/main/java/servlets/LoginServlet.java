package servlets;
import dao.DAOFactory;
import dao.UserDAO;
import beans.*;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private UserDAO userDAO;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    @Override
    public void init() throws ServletException {
        userDAO = DAOFactory.getInstance().getUserDAO();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		 String email = request.getParameter("email");
		 
	     String password = request.getParameter("password");
	     if (email == null || password == null ) {
	                request.setAttribute("error", "Email et mot de passe requis");
	                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
	                return;
	            }
	        User user = userDAO.login(email.trim(), password.trim());
	        if (user != null) {
	            HttpSession session = request.getSession();
	            session.setAttribute("user", user);
	            session.setAttribute("userId", user.getId());
	            session.setAttribute("userRole", user.getRole());
	            session.setAttribute("userName", user.getPrenom() + " " + user.getNom());            
	            String redirectUrl = getDashboardUrl(user.getRole());
	            response.sendRedirect(request.getContextPath() + redirectUrl);
	        }
	        else {
	            request.setAttribute("error", "email ou mot de passe incorrect");
	            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
	        }
	    }
	    
	    
	    private String getDashboardUrl(String role) {
	        switch (role) {
	            case "PATIENT":
	                return "/patient/dashboard";
	            case "MEDECIN":
	                return "/medecin/dashboard";
	            case "SECRETAIRE":
	                return "/secretaire/dashboard";
	            case "ADMIN":
	                return "/admin/dashboard";
	            default:
	                return "/login";
	        }
	    }

	        
	        	

}
