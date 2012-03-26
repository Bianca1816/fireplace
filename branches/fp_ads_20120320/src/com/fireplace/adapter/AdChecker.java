package com.fireplace.adapter;

import java.io.BufferedReader;
import java.io.FileReader;

public class AdChecker
{
	private static Boolean m_adsValue = null;
	private static final String m_adsChecker = "admob";
	private static final String m_adsHostFilename = "/etc/hosts";

	public static boolean isAdsDisabled()
	{
		synchronized(AdChecker.class)
		{
			if(m_adsValue == null)
			{
				m_adsValue = Boolean.FALSE;
				
				BufferedReader reader = null;
	  			try
				{
		    			reader = new BufferedReader(new FileReader(m_adsHostFilename));
			    		String line;
		    			while((line = reader.readLine()) != null)
		    			{	        
						if(line.toLowerCase().contains(m_adsChecker))
						{
							m_adsValue = Boolean.TRUE;
							break;
						}
					}
				}
				catch(Exception e)
				{
			    	
				}			    	   
				finally
				{
					if(reader != null)
				    	{
						try 
						{
							reader.close();
						}
						catch(Exception e) 
						{
						
						}
					}
			    	}
			}
			
			return m_adsValue.booleanValue();
		}
	}
}