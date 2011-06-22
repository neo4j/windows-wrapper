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

/**
 * @author Thomas Boerkel
 */
public interface WINREG
{
	public final static int	HKEY_CLASSES_ROOT	= 0x80000000;
	public final static int	HKEY_CURRENT_USER	= 0x80000001;
	public final static int	HKEY_LOCAL_MACHINE	= 0x80000002;
	public final static int	HKEY_USERS			= 0x80000003;
}
