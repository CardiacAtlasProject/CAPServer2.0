package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.net.*;
import java.util.*;
import javax.management.*;
import javax.management.modelmbean.*;
import org.jboss.jmx.adaptor.control.Server;
import org.jboss.jmx.adaptor.control.AttrResultInfo;
import org.jboss.jmx.adaptor.model.*;
import java.lang.reflect.Array;
import org.jboss.util.propertyeditor.PropertyEditors;

public final class inspectMBean_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {


   public String fixDescription(String desc)
   {
      if (desc == null || desc.equals(""))
      {
        return "(no description)";
      }
      return desc;
   }

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

      out.write('\r');
      out.write('\n');
      out.write("\r\n");
      out.write("<html>\r\n");
      out.write("<head>\r\n");
      out.write("   <title>MBean Inspector</title>\r\n");
      out.write("   <link rel=\"stylesheet\" href=\"style_master.css\" type=\"text/css\">\r\n");
      out.write("   <meta http-equiv=\"cache-control\" content=\"no-cache\">\r\n");
      out.write("</head>\r\n");
      out.write("<body>\r\n");
      out.write("\r\n");
      org.jboss.jmx.adaptor.model.MBeanData mbeanData = null;
      synchronized (request) {
        mbeanData = (org.jboss.jmx.adaptor.model.MBeanData) _jspx_page_context.getAttribute("mbeanData", PageContext.REQUEST_SCOPE);
        if (mbeanData == null){
          mbeanData = new org.jboss.jmx.adaptor.model.MBeanData();
          _jspx_page_context.setAttribute("mbeanData", mbeanData, PageContext.REQUEST_SCOPE);
        }
      }
      out.write("\r\n");
      out.write("\r\n");

   ObjectName objectName = mbeanData.getObjectName();
   String objectNameString = mbeanData.getName();
   MBeanInfo mbeanInfo = mbeanData.getMetaData();
   MBeanAttributeInfo[] attributeInfo = mbeanInfo.getAttributes();
   MBeanOperationInfo[] operationInfo = mbeanInfo.getOperations();

      out.write("\r\n");
      out.write("\r\n");
      out.write("<table width=\"100%\">\r\n");
      out.write("   <table>\r\n");
      out.write("      <tr>\r\n");
      out.write("         <td><img src=\"images/logo.gif\" align=\"left\" border=\"0\" alt=\"JBoss\"></td>\r\n");
      out.write("         <td valign=\"middle\"><h1>JMX MBean View</h1></td>\r\n");
      out.write("      <tr/>\r\n");
      out.write("   </table>\r\n");
      out.write("   <ul>\r\n");
      out.write("   <table>\r\n");
      out.write("      <tr>\r\n");
      out.write("         <td>MBean Name:</td>\r\n");
      out.write("         <td><b>Domain Name:</b></td>\r\n");
      out.write("         <td>");
      out.print( objectName.getDomain() );
      out.write("</td>\r\n");
      out.write("      </tr>\r\n");

   Hashtable properties = objectName.getKeyPropertyList();
   Iterator it = properties.keySet().iterator();
   while( it.hasNext() )
   {
      String key = (String) it.next();
      String value = (String) properties.get( key );

      out.write("\r\n");
      out.write("      <tr><td></td><td><b>");
      out.print( key );
      out.write(": </b></td><td>");
      out.print( value );
      out.write("</td></tr>\r\n");

   }

      out.write("\r\n");
      out.write("      <tr><td>MBean Java Class:</td><td colspan=\"3\">");
      out.write(org.apache.jasper.runtime.JspRuntimeLibrary.toString((((org.jboss.jmx.adaptor.model.MBeanData)_jspx_page_context.findAttribute("mbeanData")).getClassName())));
      out.write("</td></tr>\r\n");
      out.write("   </table>\r\n");
      out.write("</ul>\r\n");
      out.write("<table cellpadding=\"5\">\r\n");
      out.write("   <tr>\r\n");
      out.write("      <td><a href='HtmlAdaptor?action=displayMBeans'>Back to Agent View</a></td>\r\n");
      out.write("\t  <td>\r\n");
      out.write("      <td><a href='HtmlAdaptor?action=inspectMBean&name=");
      out.print( URLEncoder.encode(request.getParameter("name")) );
      out.write("'>Refresh MBean View</a></td>\r\n");
      out.write("   </tr>\r\n");
      out.write("</table>\r\n");
      out.write("\r\n");
      out.write("<hr>\r\n");
      out.write("<h3>MBean description:</h3>\r\n");
      out.print( fixDescription(mbeanInfo.getDescription()));
      out.write("\r\n");
      out.write("\r\n");
      out.write("<hr>\r\n");
      out.write("<h3>List of MBean attributes:</h3>\r\n");
      out.write("\r\n");
      out.write("<form method=\"post\" action=\"HtmlAdaptor\">\r\n");
      out.write("   <input type=\"hidden\" name=\"action\" value=\"updateAttributes\">\r\n");
      out.write("   <input type=\"hidden\" name=\"name\" value=\"");
      out.print( objectNameString );
      out.write("\">\r\n");
      out.write("\t<table cellspacing=\"1\" cellpadding=\"1\" border=\"1\">\r\n");
      out.write("\t\t<tr class=\"AttributesHeader\">\r\n");
      out.write("\t\t    <th>Name</th>\r\n");
      out.write("\t\t    <th>Type</th>\r\n");
      out.write("\t\t    <th>Access</th>\r\n");
      out.write("\t\t    <th>Value</th>\r\n");
      out.write("\t\t    <th>Description</th>\r\n");
      out.write("\t\t</tr>\r\n");

   boolean hasWriteable = false;
   String sep = System.getProperty("line.separator", "\n");
   for(int a = 0; a < attributeInfo.length; a ++)
   {
      MBeanAttributeInfo attrInfo = attributeInfo[a];
      String attrName = attrInfo.getName();
      String attrType = attrInfo.getType();
      AttrResultInfo attrResult = Server.getMBeanAttributeResultInfo(objectNameString, attrInfo);
      String attrValue = attrResult.getAsText();
      String access = "";
      if( attrInfo.isReadable() )
         access += "R";
      if( attrInfo.isWritable() )
      {
         access += "W";
         hasWriteable = true;
      }
      String attrDescription = fixDescription(attrInfo.getDescription());

      out.write("\r\n");
      out.write("\t\t<tr>\r\n");
      out.write("\t\t    <td>");
      out.print( attrName );
      out.write("</td>\r\n");
      out.write("\t\t    <td>");
      out.print( attrType );
      out.write("</td>\r\n");
      out.write("\t\t    <td>");
      out.print( access );
      out.write("</td>\r\n");
      out.write("          <td>\r\n");

      if( attrInfo.isWritable() )
      {
         String readonly = attrResult.editor == null ? "readonly" : "";
         if( attrType.equals("boolean") || attrType.equals("java.lang.Boolean") )
         {
            // Boolean true/false radio boxes
            Boolean value = attrValue == null || "".equals( attrValue ) ? null : Boolean.valueOf(attrValue);
            String trueChecked = (value == Boolean.TRUE ? "checked" : "");
            String falseChecked = (value == Boolean.FALSE ? "checked" : "");
            String naChecked = value == null ? "checked" : "";

      out.write("\r\n");
      out.write("            <input type=\"radio\" name=\"");
      out.print( attrName );
      out.write("\" value=\"True\" ");
      out.print(trueChecked);
      out.write(">True\r\n");
      out.write("            <input type=\"radio\" name=\"");
      out.print( attrName );
      out.write("\" value=\"False\" ");
      out.print(falseChecked);
      out.write(">False\r\n");

            // For wrappers, enable a 'null' selection
            if ( attrType.equals( "java.lang.Boolean" ) && PropertyEditors.isNullHandlingEnabled() )
            {

      out.write("\r\n");
      out.write("            <input type=\"radio\" name=\"");
      out.print( attrName );
      out.write("\" value=\"\" ");
      out.print(naChecked);
      out.write(">Null\r\n");

            }
         }
         else if( attrInfo.isReadable() )
         {  // Text fields for read-write string values
     		if (attrValue == null)
     		{

      out.write("\r\n");
      out.write("            <input type=\"text\" name=\"");
      out.print( attrName );
      out.write("\" value=\"\" ");
      out.print( readonly );
      out.write('>');
      out.write('\r');
      out.write('\n');

     		}
            else if (String.valueOf(attrValue).indexOf(sep) == -1)
            {

      out.write("\r\n");
      out.write("            <input type=\"text\" name=\"");
      out.print( attrName );
      out.write("\" value=\"");
      out.print( attrValue.replaceAll("\"","&quot;") );
      out.write('"');
      out.write(' ');
      out.print( readonly );
      out.write('>');
      out.write('\r');
      out.write('\n');

            }
            else
            {

      out.write("\r\n");
      out.write("            <textarea cols=\"40\" rows=\"5\" nowrap='nowrap' type=\"text\" name=\"");
      out.print( attrName );
      out.write('"');
      out.write(' ');
      out.print( readonly );
      out.write('>');
      out.print( attrValue );
      out.write("</textarea>\r\n");

            }
         }
         else
         {  // Empty text fields for write-only

      out.write("\r\n");
      out.write("\t\t    <input type=\"text\" name=\"");
      out.print( attrName );
      out.write('"');
      out.write(' ');
      out.print( readonly );
      out.write('>');
      out.write('\r');
      out.write('\n');

         }
      }
      else
      {
         if( attrType.equals("[Ljavax.management.ObjectName;") )
         {
            // Array of Object Names
            ObjectName[] names = (ObjectName[]) Server.getMBeanAttributeObject(objectNameString, attrName);
            if( names != null )
            {

      out.write("\r\n");
      out.write("                  <table>\r\n");

               for( int i = 0; i < names.length; i++ )
               {

      out.write("\r\n");
      out.write("                  <tr><td>\r\n");
      out.write("                  <a href=\"HtmlAdaptor?action=inspectMBean&name=");
      out.print( URLEncoder.encode(( names[ i ] + "" )) );
      out.write('"');
      out.write('>');
      out.print( ( names[ i ] + "" ) );
      out.write("</a>\r\n");
      out.write("                  </td></tr>\r\n");

               }

      out.write("\r\n");
      out.write("                  </table>\r\n");

            }
         }
         // Array of some objects
         else if( attrType.endsWith("[]"))
         {
            Object arrayObject = Server.getMBeanAttributeObject(objectNameString, attrName);
            if (arrayObject != null) {

      out.write("\r\n");
      out.write("                  <table>\r\n");

               for (int i = 0; i < Array.getLength(arrayObject); ++i) {

      out.write("\r\n");
      out.write("                  <tr><td>");
      out.print(Array.get(arrayObject,i));
      out.write("</td></tr>\r\n");

               }

      out.write("\r\n");
      out.write("                  </table>\r\n");

            } 
            
         }
         else
         {
            // Just the value string

      out.write("\r\n");
      out.write("\t\t    ");
      out.print( attrValue );
      out.write('\r');
      out.write('\n');

         }
      }
      if( attrType.equals("javax.management.ObjectName") )
      {
         // Add a link to the mbean
         if( attrValue != null )
         {

      out.write("\r\n");
      out.write("         <a href=\"HtmlAdaptor?action=inspectMBean&name=");
      out.print( URLEncoder.encode(attrValue) );
      out.write("\">View MBean</a>\r\n");

         }
      }

      out.write("\r\n");
      out.write("         </td>\r\n");
      out.write("         <td>");
      out.print( attrDescription);
      out.write("</td>\r\n");
      out.write("\t\t</tr>\r\n");

   }

      out.write("\r\n");
      out.write("\t</table>\r\n");
 if( hasWriteable )
   {

      out.write("\r\n");
      out.write("\t<input type=\"submit\" value=\"Apply Changes\">\r\n");

   }

      out.write("\r\n");
      out.write("</form>\r\n");
      out.write("\r\n");
      out.write("<hr>\r\n");
      out.write("<h3>List of MBean operations:</h3>\r\n");

   for(int a = 0; a < operationInfo.length; a ++)
   {
      MBeanOperationInfo opInfo = operationInfo[a];
      boolean accept = true;
      if (opInfo instanceof ModelMBeanOperationInfo)
      {
         Descriptor desc = ((ModelMBeanOperationInfo)opInfo).getDescriptor();
         String role = (String)desc.getFieldValue("role");
         if ("getter".equals(role) || "setter".equals(role))
         {
            accept = false;
         }
      }
      if (accept)
      {
         MBeanParameterInfo[] sig = opInfo.getSignature();

      out.write("\r\n");
      out.write("<form method=\"post\" action=\"HtmlAdaptor\">\r\n");
      out.write("   <input type=\"hidden\" name=\"action\" value=\"invokeOp\">\r\n");
      out.write("   <input type=\"hidden\" name=\"name\" value=\"");
      out.print( objectNameString );
      out.write("\">\r\n");
      out.write("   <input type=\"hidden\" name=\"methodIndex\" value=\"");
      out.print( a );
      out.write("\">\r\n");
      out.write("   <hr align='left' width='80'>\r\n");
      out.write("   <h4>");
      out.print( opInfo.getReturnType() + " " + opInfo.getName() + "()" );
      out.write("</h4>\r\n");
      out.write("   <p>");
      out.print( fixDescription(opInfo.getDescription()));
      out.write("</p>\r\n");

         if( sig.length > 0 )
         {

      out.write("\r\n");
      out.write("\t<table cellspacing=\"2\" cellpadding=\"2\" border=\"1\">\r\n");
      out.write("\t\t<tr class=\"OperationHeader\">\r\n");
      out.write("\t\t\t<th>Param</th>\r\n");
      out.write("\t\t\t<th>ParamType</th>\r\n");
      out.write("\t\t\t<th>ParamValue</th>\r\n");
      out.write("\t\t\t<th>ParamDescription</th>\r\n");
      out.write("\t\t</tr>\r\n");

            for(int p = 0; p < sig.length; p ++)
            {
               MBeanParameterInfo paramInfo = sig[p];
               String pname = paramInfo.getName();
               String ptype = paramInfo.getType();
               if( pname == null || pname.length() == 0 || pname.equals(ptype) )
               {
                  pname = "arg"+p;
               }

      out.write("\r\n");
      out.write("\t\t<tr>\r\n");
      out.write("\t\t\t<td>");
      out.print( pname );
      out.write("</td>\r\n");
      out.write("\t\t   <td>");
      out.print( ptype );
      out.write("</td>\r\n");
      out.write("         <td> \r\n");

                if( ptype.equals("boolean") || ptype.equals("java.lang.Boolean") )
                {
                   // Boolean true/false radio boxes

      out.write("\r\n");
      out.write("            <input type=\"radio\" name=\"arg");
      out.print( p);
      out.write("\" value=\"True\"checked>True\r\n");
      out.write("            <input type=\"radio\" name=\"arg");
      out.print( p);
      out.write("\" value=\"False\">False\r\n");

                 }
                 else
                 {

      out.write("\r\n");
      out.write("            <input type=\"text\" name=\"arg");
      out.print( p);
      out.write("\">\r\n");

                  }

      out.write("\r\n");
      out.write("         </td>\r\n");
      out.write("         <td>");
      out.print( fixDescription(paramInfo.getDescription()));
      out.write("</td>\r\n");
      out.write("\t\t</tr>\r\n");

               }

      out.write("\r\n");
      out.write("\t</table>\r\n");

         }

      out.write("\r\n");
      out.write("\t<input type=\"submit\" value=\"Invoke\">\r\n");
      out.write("</form>\r\n");

      }
   }

      out.write("\r\n");
      out.write("</td></tr>\r\n");
      out.write("</table>\r\n");
      out.write("</body>\r\n");
      out.write("</html>\r\n");
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
