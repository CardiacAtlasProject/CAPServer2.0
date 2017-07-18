package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.net.InetAddress;

public final class login_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List _jspx_dependants;

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.AnnotationProcessor _jsp_annotationprocessor;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_annotationprocessor = (org.apache.AnnotationProcessor) getServletConfig().getServletContext().getAttribute(org.apache.AnnotationProcessor.class.getName());
  }

  public void _jspDestroy() {
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n");
      out.write("\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");
      out.write("\r\n");
      out.write("<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:wicket=\"http://wicket.apache.org\">\n");
      out.write("    ");
      org.dcm4chee.web.common.login.LoginResources login = null;
      synchronized (request) {
        login = (org.dcm4chee.web.common.login.LoginResources) _jspx_page_context.getAttribute("login", PageContext.REQUEST_SCOPE);
        if (login == null){
          login = new org.dcm4chee.web.common.login.LoginResources();
          _jspx_page_context.setAttribute("login", login, PageContext.REQUEST_SCOPE);
        }
      }
      out.write("\r\n");
      out.write("    ");
 
     String nodeInfo = System.getProperty("dcm4che.archive.nodename", InetAddress.getLocalHost().getHostName() );
     Cookie[] cookies = request.getCookies();
     String userName = "";
     String focus = "self.focus();document.login.j_username.focus()";
     boolean darkroom = false;
     if (cookies != null) {
	     int count = 0;
	     for (int i = 0; i < cookies.length; i++) {
	         if (cookies[i].getName().equals("WEB3LOCALE")) {
	             login.setLocale(cookies[i].getValue());
	             count++;
	             if (count==3)
	             	break;
	         } else if (cookies[i].getName().equals("WEB3_CSS")) {
	             if ("base-style-r.css".equals(cookies[i].getValue()))
	                 darkroom = true;
	             count++;
	             if (count==3)
		            break;
	         } else if (cookies[i].getName().equals("signInPanel.signInForm.username")) {
	             userName = cookies[i].getValue();
	             if (userName!=null && userName.length()>0)
	                 focus = "self.focus();document.login.j_username.value='"+userName+
	                 	"';document.login.j_password.focus()";
	             count++;
	             if (count==3)
	             	break;
	         }
	     }
     }
    
      out.write("\r\n");
      out.write("    <head>\n");
      out.write("\t    <title>");
      out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${login.browser_title}", java.lang.String.class, (PageContext)_jspx_page_context, null, false));
      out.write(' ');
      out.write('(');
      out.print( nodeInfo );
      out.write(")</title>\n");
      out.write("\t    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n");
      out.write("        ");
 if (darkroom) { 
      out.write("\n");
      out.write("           <link rel=\"stylesheet\" type=\"text/css\" href=\"resources/org.dcm4chee.web.common.base.BaseWicketPage/base-style-r.css\" />\n");
      out.write("        ");
 } else {
      out.write("\n");
      out.write("           <link rel=\"stylesheet\" type=\"text/css\" href=\"resources/org.dcm4chee.web.common.base.BaseWicketPage/base-style.css\" />\n");
      out.write("  \t    ");
 } 
      out.write("\n");
      out.write("        <script>\n");
      out.write("            function login_init() {\n");
      out.write("            \t");
      out.print( focus );
      out.write("\n");
      out.write("                window.setTimeout(\"location.reload(true);\", ");
      out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${pageContext.session.maxInactiveInterval * 1000 - 5000}", java.lang.String.class, (PageContext)_jspx_page_context, null, false));
      out.write(");\n");
      out.write("            }\n");
      out.write("        </script>\n");
      out.write("    </head>\n");
      out.write("    <body onload=\"login_init();\">\n");
      out.write("        <div class=\"tabpanel\">\n");
      out.write("            <div class=\"module-selector\">\n");
      out.write("                <div class=\"tab-row\">\n");
      out.write("\t\t\t        <ul>\n");
      out.write("\t\t            </ul>\n");
      out.write("                </div>\n");
      out.write("\t\t        <div class=\"tab-logo\" style=\"float: right; margin-top: 15px; height: 43px; padding-right: 15px; padding-left: 15px;\">\n");
      out.write("\t\t           <img alt=\"dcm4che.org\" src=\"resources/org.dcm4chee.web.common.base.BaseWicketPage/images/logo.gif\" /><br/>\n");
      out.write("\t\t        </div>\n");
      out.write("\t       </div>\n");
      out.write("\t       <div class=\"module-panel\"></div>\n");
      out.write("        </div>\n");
      out.write("        <div class=\"signin\" style=\"padding-top: 160px;\">\n");
      out.write("            ");
 if (request.getParameter("loginFailed") == null) { 
      out.write("\n");
      out.write("\t            <span class=\"login-desc\">");
      out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${login.loginLabel}", java.lang.String.class, (PageContext)_jspx_page_context, null, false));
      out.write(' ');
      out.print( nodeInfo );
      out.write("</span>\n");
      out.write("            ");
 } else {
      out.write("\n");
      out.write("    \t        <span class=\"login-desc\">");
      out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${login.loginFailed}", java.lang.String.class, (PageContext)_jspx_page_context, null, false));
      out.write(' ');
      out.print( nodeInfo );
      out.write("</span>\n");
      out.write("    \t    ");
 } 
      out.write("\n");
      out.write("            <div>\n");
      out.write("\t\t        <form action=\"j_security_check\" method=\"POST\" name=\"login\" >\r\n");
      out.write("\t\t            <table style=\"padding-top: 60px; padding-right: 90px; padding-bottom: 10px;\">\n");
      out.write("                        <tbody>\n");
      out.write("\t\t\t                <tr style=\"text-align: left;\">\n");
      out.write("\t\t\t                    <td align=\"right\">");
      out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${login.username}", java.lang.String.class, (PageContext)_jspx_page_context, null, false));
      out.write("</td>\n");
      out.write("\t\t\t                    <td>\n");
      out.write("\t\t\t                        <input type=\"text\" name=\"j_username\" size=\"30\" />\n");
      out.write("\t\t\t                    </td>\n");
      out.write("\t\t\t                </tr>\n");
      out.write("\t\t\t                <tr style=\"text-align: left;\">\n");
      out.write("\t\t\t                    <td align=\"right\">");
      out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${login.password}", java.lang.String.class, (PageContext)_jspx_page_context, null, false));
      out.write("</td>\n");
      out.write("\t\t\t                    <td>\n");
      out.write("\t\t\t                        <input type=\"password\" name=\"j_password\" size=\"30\" />\n");
      out.write("\t\t\t                    </td>\n");
      out.write("\t\t\t                </tr>\n");
      out.write("\t\t\t                <tr style=\"text-align: left;\">\n");
      out.write("\t\t\t                    <td></td>\n");
      out.write("\t\t\t                    <td>\n");
      out.write("\t\t\t                        <input type=\"submit\" name=\"submit\" value=\"");
      out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${login.submit}", java.lang.String.class, (PageContext)_jspx_page_context, null, false));
      out.write("\" class=\"button\" />\n");
      out.write("\t\t\t                        <input type=\"reset\" value=\"");
      out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${login.reset}", java.lang.String.class, (PageContext)_jspx_page_context, null, false));
      out.write("\" onclick=\"document.login.j_username.focus()\" class=\"button\"/>\n");
      out.write("\t\t\t                    </td>\n");
      out.write("\t\t\t                </tr>\n");
      out.write("                        </tbody>\n");
      out.write("                    </table>\n");
      out.write("\t\t        </form>\n");
      out.write("            </div>\n");
      out.write("        </div>\n");
      out.write("  </body>\n");
      out.write("</html>\n");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try { out.clearBuffer(); } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
