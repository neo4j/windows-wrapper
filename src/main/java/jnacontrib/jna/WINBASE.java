/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package jnacontrib.jna;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * @author Thomas Boerkel
 */

public interface WINBASE
{
	/*
	 * typedef struct _SECURITY_ATTRIBUTES { DWORD nLength; LPVOID
	 * lpSecurityDescriptor; BOOL bInheritHandle; } SECURITY_ATTRIBUTES,
	 * PSECURITY_ATTRIBUTES,LPSECURITY_ATTRIBUTES;
	 */
	public static class SECURITY_ATTRIBUTES extends Structure
	{
		public int		nLength;
		public Pointer	lpSecurityDescriptor;
		public boolean	bInheritHandle;
	}

	/*
	 * typedef struct _FILETIME { DWORD dwLowDateTime; DWORD dwHighDateTime; }
	 * FILETIME, *PFILETIME, *LPFILETIME;
	 */
	public static class FILETIME extends Structure
	{
		public int	dwLowDateTime;
		public int	dwHighDateTime;
	}
}
