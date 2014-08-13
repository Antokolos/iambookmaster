package com.iambookmaster.server.applets;

import java.applet.Applet;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStreamWriter;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.swing.JFileChooser;

import com.iambookmaster.client.common.Base64Coder;

public class FileExchange extends Applet{
	private static final long serialVersionUID = 1L;
	
	public static String RESULT_OK="OK";
	
	protected String buffer;
	
	public String selectFilesByExtention(final String path,final String extension) {
		Object res = AccessController.doPrivileged(new PrivilegedAction() {
	        public Object run() {
				try {
					buffer = null;
					File file = new File(path);
					if (file.isDirectory()==false) {
						return path+" is not a directory";
					} else if (file.canRead()==false) {
						return path+" cannot be read";
					}
					File[] files = file.listFiles(new FilenameFilter() {
						public boolean accept(File dir, String name) {
							int i = name.lastIndexOf('.');
							return i>0 && extension.equals(name.substring(i+1).toLowerCase());
						}
					});
					StringBuffer stringBuffer = new StringBuffer(2000);
					for (int i = 0; i < files.length; i++) {
						if (stringBuffer.length()>0) {
							stringBuffer.append('\n');
						}
						stringBuffer.append(files[i].getAbsolutePath());
					}
					buffer = stringBuffer.toString();
					return RESULT_OK;
				} catch (Exception e) {
					return e.getMessage();
				}
	        }
	    });			
		return String.valueOf(res);
	}
	
	
	public String selectFolder(final String title,final String button,final String def) {
		Object res = AccessController.doPrivileged(new PrivilegedAction() {
	        public Object run() {
				try {
					buffer = null;
					JFileChooser chooser = new JFileChooser();
					if (def != null) {
						chooser.setCurrentDirectory(new File(def));
					}
					chooser.setDialogTitle(title);
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					Frame parent = new Frame();
					int retval = chooser.showDialog(parent, button);
					if (retval == JFileChooser.APPROVE_OPTION) {
						buffer = chooser.getSelectedFile().getAbsolutePath();
						return RESULT_OK;
					} else {
						return null;
					}
				} catch (Exception e) {
					return e.getMessage();
				}
	        }
	    });			
		return String.valueOf(res);
	}
	
	
	public String selectfile(final String title,final boolean save) {
		Object res = AccessController.doPrivileged(new PrivilegedAction() {
	        public Object run() {
				try {
					buffer = null;
					Frame parent = new Frame();
					FileDialog fd = new FileDialog(parent, title,save ? FileDialog.SAVE : FileDialog.LOAD);
					fd.setVisible(true);
					String selectedItem = fd.getFile();
					if (selectedItem == null) {
						return null;
					} else {
						buffer = fd.getDirectory()+selectedItem;
						return RESULT_OK;
					}
				} catch (Exception e) {
					return e.getMessage();
				}
	        }
	    });			
		return String.valueOf(res);
	}
	
	public String readfile(final String loc) {
		Object res = AccessController.doPrivileged(new PrivilegedAction() {
	        public Object run() {
				try {
					FileInputStream stream = new FileInputStream(loc);
					int size = stream.available();
					byte[] buf = new byte[size];
					stream.read(buf);
					stream.close();
					buffer = new String(buf,"UTF-8");
					buf = null;
					return RESULT_OK;
				} catch (Exception e) {
					return e.getMessage();
				}
	        }
	    });			
		return String.valueOf(res);
	}
		
	public String writefile(final String file) {
		Object res = AccessController.doPrivileged(new PrivilegedAction() {
	        public Object run() {
				try {
					BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
					out.write(buffer);
					out.close();
					buffer = null;
					return RESULT_OK;
				} catch (Exception e) {
					return e.getMessage();
				}
	        }
	    });			
		return String.valueOf(res);
	}
		
	public String getBuffer() {
		return buffer;
	}

	public String getBuffer64() {
		return Base64Coder.encodeString(buffer);
	}

	public void setBuffer(String buffer) {
		this.buffer = buffer;
	}

}