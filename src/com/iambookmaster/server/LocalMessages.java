package com.iambookmaster.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import com.google.gwt.i18n.client.Constants.DefaultStringValue;
import com.google.gwt.i18n.client.Messages.DefaultMessage;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.remote.RemotePanel;
import com.iambookmaster.server.tags.MessageTag;

public class LocalMessages implements ServletContextListener,Serializable{

	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(LocalMessages.class.getName());
	
	private static HashMap<String,Properties> constansts; 
	private static HashMap<String,Properties> messages;
	private static Properties defaultConstansts; 
	private static Properties defaultMessages;

	public static final String DEFAULT_LOCALE = "";

	private static final String LOCALE_IN_SESSION = LocalMessages.class.getName();

	public void contextDestroyed(ServletContextEvent arg0) {
	}

	public void contextInitialized(ServletContextEvent arg0) {
		try {
			defaultConstansts = getProps(AppConstants.class, DEFAULT_LOCALE);
			defaultMessages = getProps(AppMessages.class, DEFAULT_LOCALE);
			String locales = arg0.getServletContext().getInitParameter("locales");
			if (locales != null) {
				String[] locs = locales.split(",");
				constansts = new HashMap<String, Properties>();
				messages = new HashMap<String, Properties>();
				for (String locale:locs) {
					constansts.put(locale, getProps(AppConstants.class, locale));
					messages.put(locale, getProps(AppMessages.class, locale));
				}
			}
		} catch (Exception e) {
			log.log(Level.SEVERE,"Cannot load locales");
			log.log(Level.SEVERE,e.getMessage());
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private Properties getProps(Class class1, String locale) throws IOException{
		StringBuffer buffer = new StringBuffer(class1.getName().replace('.', '/'));
		if (locale.length()>0) {
			buffer.append('_');
			buffer.append(locale);
		}
		buffer.append(".properties");
		Properties props = new Properties();
		InputStream stream = MessageTag.class.getClassLoader().getResourceAsStream(buffer.toString());
		if (stream !=null) {
			InputStreamReader reader = new InputStreamReader(stream,"UTF-8");
			//this is for JDK 1.6
			//props.load(reader);
			
			//this is for JDK 1.5
			load(props,reader);
			reader.close();
			stream.close();
		}
		if (DEFAULT_LOCALE.equals(locale)) {
			Method[] methods = class1.getDeclaredMethods();
			for (Method method:methods) {
				if (props.containsKey(method.getName())==false) {
					DefaultStringValue annotation = method.getAnnotation(DefaultStringValue.class);
					if (annotation!=null) {
						props.setProperty(method.getName(), annotation.value());
					}
					DefaultMessage defaultMessage = method.getAnnotation(DefaultMessage.class);
					if (defaultMessage!=null) {
						props.setProperty(method.getName(), defaultMessage.value());
					}
				}
			}
		}
		return props;
	}
	
	public static String getConstant(String key,String locale) {
		if (locale==null) {
			return defaultConstansts.getProperty(key);
		} else {
			Properties properties = constansts.get(locale);
			if (properties==null) {
				throw new IllegalArgumentException("Unknown locale "+locale);
			} else {
				String val = properties.getProperty(key);
				if (val==null) {
					return defaultConstansts.getProperty(key);
				} else {
					return val;
				}
			}
		}
	}
	
	public static String getMessageText(String key,String locale) {
		if (locale==null) {
			return defaultMessages.getProperty(key);
		} else {
			Properties properties = messages.get(locale);
			if (properties==null) {
				throw new IllegalArgumentException("Unknown locale "+locale);
			} else {
				String val = properties.getProperty(key);
				if (val==null) {
					return defaultMessages.getProperty(key);
				} else {
					return val;
				}
			}
		}
	}
	public static String getMessage(String key,String locale,Object... args) {
		String text = getMessageText(key, locale);
		if (text==null) {
			throw new IllegalArgumentException("Unknown message key:"+key);
		} else {
			int j = 0;
			int i = text.indexOf("{",j);
			if (i>=0) {
				StringBuffer buffer = new StringBuffer(text);
				while (true) {
					int k = i+1;
					j=buffer.indexOf("}",k);
					if (j<0) {
						//only {
						throw new IllegalArgumentException("Unclosed argument (no '}') in message with key '"+key+"'");
					} 
					try {
						k = Integer.parseInt(buffer.substring(k, j));
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException("Incorrect argument number '"+buffer.substring(i,j+1)+"' in message with key '"+key+"'");
					}
					j++;
					if (k<0 || k>=args.length) {
						throw new IllegalArgumentException("Argument "+k+" was not provided for message with key '"+key+"'");
					}
					String val;
					if (args[k] instanceof String) {
						val = (String) args[k];
					} else if (args[k]==null){
						val = "";
					} else {
						val = args[k].toString();
					}
					buffer.replace(i, j, val);
					j = i+val.length();
					i = buffer.indexOf("{",j);
					if (i<0) {
						break;
					}
				}
				return buffer.toString();
			} else if (args.length==0){
				//no parameters
				return text;
			} else {
				//wrong parameters
				throw new IllegalArgumentException("Message with key '"+key+"' expects no arguments");
			}
		}
	}

	public static String getLocale(HttpServletRequest request,HttpServletResponse response) {
		String locale = (String)request.getSession().getAttribute(LOCALE_IN_SESSION);
		if (locale==null) {
			locale = request.getParameter(RemotePanel.LOCALE_IN_REQUEST);
		} else {
			return locale;
		}
		if (locale==null) {
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie cookie:cookies) {
					if (cookie.getName().equals(LOCALE_IN_SESSION)) {
						locale = cookie.getValue();
						break;
					}
				}
			}
		}
		if (locale==null) {
			locale = request.getLocale().getLanguage();
		}
		if (constansts.containsKey(locale)) {
			response.addCookie(new Cookie(LOCALE_IN_SESSION,locale));
			request.getSession().setAttribute(LOCALE_IN_SESSION,locale);
			return locale;
		} else {
			int i = locale.indexOf('_');
			if (i<0) {
				return null;
			} else {
				locale = locale.substring(0,i);
				if (constansts.containsKey(locale)) {
					response.addCookie(new Cookie(LOCALE_IN_SESSION,locale));
					request.getSession().setAttribute(LOCALE_IN_SESSION,locale);
					return locale;
				} else {
					return null;
				}
			}
		}
	}

	public static String getLocale(PageContext pageContext) {
		return getLocale((HttpServletRequest)pageContext.getRequest(),(HttpServletResponse)pageContext.getResponse());
	}

	
	@SuppressWarnings("unchecked")
	public static <T> T getInstance(Class<T>  class1,String locale) {
		Class[] inters = class1.getInterfaces();
		Class[] all = new Class[inters.length+1];
		System.arraycopy(inters, 0, all, 0, inters.length);
		all[inters.length] = class1;
		Object proxy = java.lang.reflect.Proxy.newProxyInstance(
				class1.getClassLoader(),
				all,
			    new LocaleProxy(locale)); 
		return (T)proxy;
	}

	public void initialize(String locale) throws IOException {
		defaultConstansts = getProps(AppConstants.class, DEFAULT_LOCALE);
		defaultMessages = getProps(AppMessages.class, DEFAULT_LOCALE);
		constansts = new HashMap<String, Properties>();
		messages = new HashMap<String, Properties>();
		constansts.put(locale, getProps(AppConstants.class, locale));
		messages.put(locale, getProps(AppMessages.class, locale));
	}
	
	/**
	 * It is just for JDK 1.5
	 * The source was takes from JDK 1.5 sources
	 * @param inStream
	 * @throws IOException
	 */
    private void load(Properties properties,Reader reader) throws IOException {
        char[] convtBuf = new char[1024];
        LineReader lr = new LineReader(reader);

        int limit;
        int keyLen;
        int valueStart;
        char c;
        boolean hasSep;
        boolean precedingBackslash;

        while ((limit = lr.readLine()) >= 0) {
            c = 0;
            keyLen = 0;
            valueStart = limit;
            hasSep = false;

	    //System.out.println("line=<" + new String(lineBuf, 0, limit) + ">");
            precedingBackslash = false;
            while (keyLen < limit) {
                c = lr.lineBuf[keyLen];
                //need check if escaped.
                if ((c == '=' ||  c == ':') && !precedingBackslash) {
                    valueStart = keyLen + 1;
                    hasSep = true;
                    break;
                } else if ((c == ' ' || c == '\t' ||  c == '\f') && !precedingBackslash) {
                    valueStart = keyLen + 1;
                    break;
                } 
                if (c == '\\') {
                    precedingBackslash = !precedingBackslash;
                } else {
                    precedingBackslash = false;
                }
                keyLen++;
            }
            while (valueStart < limit) {
                c = lr.lineBuf[valueStart];
                if (c != ' ' && c != '\t' &&  c != '\f') {
                    if (!hasSep && (c == '=' ||  c == ':')) {
                        hasSep = true;
                    } else {
                        break;
                    }
                }
                valueStart++;
            }
            String key = loadConvert(lr.lineBuf, 0, keyLen, convtBuf);
            String value = loadConvert(lr.lineBuf, valueStart, limit - valueStart, convtBuf);
            properties.put(key, value);
        }
    }

    /** 
	 * It is just for JDK 1.5
	 * The source was takes from JDK 1.5 sources
     * Read in a "logical line" from an InputStream/Reader, skip all comment
     * and blank lines and filter out those leading whitespace characters 
     * (\u0020, \u0009 and \u000c) from the beginning of a "natural line". 
     * Method returns the char length of the "logical line" and stores 
     * the line in "lineBuf". 
     */
    class LineReader {
        public LineReader(Reader reader) {
            this.reader = reader;
            inCharBuf = new char[8192]; 
        }

        byte[] inByteBuf;
        char[] inCharBuf;
        char[] lineBuf = new char[1024];
        int inLimit = 0;
        int inOff = 0;
        InputStream inStream;
        Reader reader;

        int readLine() throws IOException {
            int len = 0;
            char c = 0;

            boolean skipWhiteSpace = true;
            boolean isCommentLine = false;
            boolean isNewLine = true;
            boolean appendedLineBegin = false;
            boolean precedingBackslash = false;
	    boolean skipLF = false;

            while (true) {
                if (inOff >= inLimit) {
                    inLimit = (inStream==null)?reader.read(inCharBuf)
		                              :inStream.read(inByteBuf);
		    inOff = 0;
		    if (inLimit <= 0) {
			if (len == 0 || isCommentLine) { 
			    return -1; 
			}
			return len;
		    }
		}     
                if (inStream != null) {
                    //The line below is equivalent to calling a 
                    //ISO8859-1 decoder.
	            c = (char) (0xff & inByteBuf[inOff++]);
                } else {
                    c = inCharBuf[inOff++];
                }
                if (skipLF) {
                    skipLF = false;
		    if (c == '\n') {
		        continue;
		    }
		}
		if (skipWhiteSpace) {
		    if (c == ' ' || c == '\t' || c == '\f') {
			continue;
		    }
		    if (!appendedLineBegin && (c == '\r' || c == '\n')) {
			continue;
		    }
		    skipWhiteSpace = false;
		    appendedLineBegin = false;
		}
		if (isNewLine) {
		    isNewLine = false;
		    if (c == '#' || c == '!') {
			isCommentLine = true;
			continue;
		    }
		}
		
		if (c != '\n' && c != '\r') {
		    lineBuf[len++] = c;
		    if (len == lineBuf.length) {
		        int newLength = lineBuf.length * 2;
		        if (newLength < 0) {
		            newLength = Integer.MAX_VALUE;
		        }
			char[] buf = new char[newLength];
			System.arraycopy(lineBuf, 0, buf, 0, lineBuf.length);
			lineBuf = buf;
		    }
		    //flip the preceding backslash flag
		    if (c == '\\') {
			precedingBackslash = !precedingBackslash;
		    } else {
			precedingBackslash = false;
		    }
		}
		else {
		    // reached EOL
		    if (isCommentLine || len == 0) {
			isCommentLine = false;
			isNewLine = true;
			skipWhiteSpace = true;
			len = 0;
			continue;
		    }
		    if (inOff >= inLimit) {
                        inLimit = (inStream==null)
                                  ?reader.read(inCharBuf)
			          :inStream.read(inByteBuf);
			inOff = 0;
			if (inLimit <= 0) {
			    return len;
			}
		    }
		    if (precedingBackslash) {
			len -= 1;
			//skip the leading whitespace characters in following line
			skipWhiteSpace = true;
			appendedLineBegin = true;
			precedingBackslash = false;
			if (c == '\r') {
                            skipLF = true;
			}
		    } else {
			return len;
		    }
		}
	    }
	}
    }
    
    /**
	 * It is just for JDK 1.5
	 * The source was takes from JDK 1.5 sources
     * Converts encoded &#92;uxxxx to unicode chars
     * and changes special saved chars to their original forms
     */
    private String loadConvert (char[] in, int off, int len, char[] convtBuf) {
        if (convtBuf.length < len) {
            int newLen = len * 2;
            if (newLen < 0) {
	        newLen = Integer.MAX_VALUE;
	    } 
	    convtBuf = new char[newLen];
        }
        char aChar;
        char[] out = convtBuf; 
        int outLen = 0;
        int end = off + len;

        while (off < end) {
            aChar = in[off++];
            if (aChar == '\\') {
                aChar = in[off++];   
                if(aChar == 'u') {
                    // Read the xxxx
                    int value=0;
		    for (int i=0; i<4; i++) {
		        aChar = in[off++];  
		        switch (aChar) {
		          case '0': case '1': case '2': case '3': case '4':
		          case '5': case '6': case '7': case '8': case '9':
		             value = (value << 4) + aChar - '0';
			     break;
			  case 'a': case 'b': case 'c':
                          case 'd': case 'e': case 'f':
			     value = (value << 4) + 10 + aChar - 'a';
			     break;
			  case 'A': case 'B': case 'C':
                          case 'D': case 'E': case 'F':
			     value = (value << 4) + 10 + aChar - 'A';
			     break;
			  default:
                              throw new IllegalArgumentException(
                                           "Malformed \\uxxxx encoding.");
                        }
                     }
                    out[outLen++] = (char)value;
                } else {
                    if (aChar == 't') aChar = '\t'; 
                    else if (aChar == 'r') aChar = '\r';
                    else if (aChar == 'n') aChar = '\n';
                    else if (aChar == 'f') aChar = '\f'; 
                    out[outLen++] = aChar;
                }
            } else {
	        out[outLen++] = (char)aChar;
            }
        }
        return new String (out, 0, outLen);
    }
    
}
