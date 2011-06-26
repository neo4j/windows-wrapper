package org.rzo.yajsw;
/* This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import jnacontrib.jna.Advapi32;
import jnacontrib.jna.Options;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

// TODO: Auto-generated Javadoc
/**
 * The Class WindowsXPProcess.
 */
public class WindowsXPProcess extends AbstractProcess
{

	/**
	 * The Interface MyUser32.
	 */
	public interface MyUser32 extends User32
	{
		// Method declarations, constant and structure definitions go here

		/** The INSTANCE. */
		MyUser32	INSTANCE	= (MyUser32) Native.loadLibrary("User32", MyUser32.class);

		/*
		 * HWND GetForegroundWindow(VOID);
		 */
		/**
		 * Gets the foreground window.
		 *
		 * @return the pointer
		 */
		Pointer GetForegroundWindow();

		/*
		 * DWORD GetWindowThreadProcessId( HWND hWnd, LPDWORD lpdwProcessId );
		 */
		/**
		 * Gets the window thread process id.
		 *
		 * @param hWnd
		 *            the h wnd
		 * @param lpdwProcessId
		 *            the lpdw process id
		 *
		 * @return the int
		 */
		int GetWindowThreadProcessId(Pointer hWnd, IntByReference lpdwProcessId);

		/** The W m_ close. */
		int	WM_CLOSE	= 16;
		int	WM_QUIT		= 18;
		int	WM_DESTROY	= 2;

		/*
		 * BOOL PostThreadMessage( DWORD idThread, UINT Msg, WPARAM wParam,
		 * LPARAM lParam );
		 */
		/**
		 * Post thread message a.
		 *
		 * @param idThread
		 *            the id thread
		 * @param Msg
		 *            the msg
		 * @param wParam
		 *            the w param
		 * @param lParam
		 *            the l param
		 *
		 * @return true, if successful
		 */
		boolean PostThreadMessageA(int idThread, int Msg, int wParam, int lParam);

		/*
		 * DWORD WINAPI WaitForInputIdle( __in HANDLE hProcess, __in DWORD
		 * dwMilliseconds );
		 */
		/**
		 * Wait for input idle.
		 *
		 * @param hProcess
		 *            the h process
		 * @param dwMilliseconds
		 *            the dw milliseconds
		 *
		 * @return the int
		 */
		int WaitForInputIdle(HANDLE hProcess, int dwMilliseconds);

		void PostMessageA(Pointer hWnd, int msg, Pointer wParam, Pointer lParam);

		public interface WNDENUMPROC extends StdCallCallback
		{
			/** Return whether to continue enumeration. */
			boolean callback(Pointer hWnd, int data);
		}

		boolean EnumWindows(WNDENUMPROC lpEnumFunc, int data);

	}

	/**
	 * The Interface MyKernel32.
	 */
	public interface MyKernel32 extends com.sun.jna.platform.win32.Kernel32
	{

		// Method declarations, constant and structure definitions go here

		/** The INSTANCE. */
		MyKernel32	INSTANCE	= (MyKernel32) Native.loadLibrary("kernel32", MyKernel32.class);

		/*
		 * BOOL WINAPI ReadFile( __in HANDLE hFile, __out LPVOID lpBuffer, __in
		 * DWORD nNumberOfBytesToRead, __out_opt LPDWORD lpNumberOfBytesRead,
		 * __inout_opt LPOVERLAPPED lpOverlapped );
		 */

		boolean ReadFile(Pointer hFile, Memory lpBuffer, int nNumberOfBytesToRead, IntByReference lpNumberOfBytesRead, Structure lpOverlapped);

		/*
		 * DWORD WINAPI GetCurrentProcessId(void);
		 */
		/*
		 * (non-Javadoc)
		 *
		 * @see com.sun.jna.examples.win32.Kernel32#GetCurrentProcessId()
		 */
		int GetCurrentProcessId();

		/*
		 * DWORD WINAPI GetProcessIdOfThread( __in HANDLE Thread );
		 */
		/**
		 * Gets the process id of thread.
		 *
		 * @param Thread
		 *            the thread
		 *
		 * @return the int
		 */
		int GetProcessIdOfThread(Pointer Thread);

		/*
		 * BOOL WINAPI CreateProcess( LPCTSTR lpApplicationName, LPTSTR
		 * lpCommandLine, LPSECURITY_ATTRIBUTES lpProcessAttributes,
		 * LPSECURITY_ATTRIBUTES lpThreadAttributes, BOOL bInheritHandles, DWORD
		 * dwCreationFlags, LPVOID lpEnvironment, LPCTSTR lpCurrentDirectory,
		 * LPSTARTUPINFO lpStartupInfo, LPPROCESS_INFORMATION
		 * lpProcessInformation );
		 */
		/**
		 * Creates the process a.
		 *
		 * @param lpApplicationName
		 *            the lp application name
		 * @param lpCommandLine
		 *            the lp command line
		 * @param lpProcessAttributes
		 *            the lp process attributes
		 * @param lpThreadAttributes
		 *            the lp thread attributes
		 * @param bInheritHandles
		 *            the b inherit handles
		 * @param dwCreationFlags
		 *            the dw creation flags
		 * @param lpEnvironment
		 *            the lp environment
		 * @param lpCurrentDirectory
		 *            the lp current directory
		 * @param lpStartupInfo
		 *            the lp startup info
		 * @param lpProcessInformation
		 *            the lp process information
		 *
		 * @return true, if successful
		 */
		boolean CreateProcessA(String lpApplicationName, String lpCommandLine, Structure lpProcessAttributes, Structure lpThreadAttributes,
				boolean bInheritHandles, int dwCreationFlags, Structure lpEnvironment, String lpCurrentDirectory, Structure lpStartupInfo,
				Structure lpProcessInformation);

		boolean CreateProcessW(WString lpApplicationName, WString lpCommandLine, Structure lpProcessAttributes, Structure lpThreadAttributes,
				boolean bInheritHandles, int dwCreationFlags, Memory lpEnvironment, WString lpCurrentDirectory, Structure lpStartupInfo,
				Structure lpProcessInformation);

		/** The CREAT e_ n o_ window. */
		int	CREATE_NO_WINDOW			= 0x08000000;

		/** The CREAT e_ unicod e_ environment. */
		int	CREATE_UNICODE_ENVIRONMENT	= 0x00000400;

		/** The CREAT e_ ne w_ console. */
		int	CREATE_NEW_CONSOLE			= 0x00000010;

		int	DETACHED_PROCESS			= 0x00000008;

		/*
		 * typedef struct _PROCESS_INFORMATION { HANDLE hProcess; HANDLE
		 * hThread; DWORD dwProcessId; DWORD dwThreadId; }
		 */

		/**
		 * The Class PROCESS_INFORMATION.
		 */
		public static class PROCESS_INFORMATION extends Structure
		{

			/** The h process. */
			public HANDLE	hProcess	= null;

			/** The h thread. */
			public Pointer	hThread		= null;

			/** The dw process id. */
			public int		dwProcessId	= -1;

			/** The dw thread id. */
			public int		dwThreadId	= -1;

		}

		/*
		 * typedef struct _STARTUPINFO { DWORD cb; LPTSTR lpReserved; LPTSTR
		 * lpDesktop; LPTSTR lpTitle; DWORD dwX; DWORD dwY; DWORD dwXSize; DWORD
		 * dwYSize; DWORD dwXCountChars; DWORD dwYCountChars; DWORD
		 * dwFillAttribute; DWORD dwFlags; WORD wShowWindow; WORD cbReserved2;
		 * LPBYTE lpReserved2; HANDLE hStdInput; HANDLE hStdOutput; HANDLE
		 * hStdError; }
		 */

		/**
		 * The Class STARTUPINFO.
		 */
		public static class STARTUPINFO extends Structure
		{

			/** The cb. */
			public int		cb;

			/** The lp reserved. */
			public WString	lpReserved;

			/** The lp desktop. */
			public WString	lpDesktop;

			/** The lp title. */
			public WString	lpTitle;

			/** The dw x. */
			public int		dwX;

			/** The dw y. */
			public int		dwY;

			/** The dw x size. */
			public int		dwXSize;

			/** The dw y size. */
			public int		dwYSize;

			/** The dw x count chars. */
			public int		dwXCountChars;

			/** The dw y count chars. */
			public int		dwYCountChars;

			/** The dw fill attribute. */
			public int		dwFillAttribute;

			/** The dw flags. */
			public int		dwFlags;

			/** The w show window. */
			public short	wShowWindow;

			/** The cb reserved2. */
			public short	cbReserved2;

			/** The lp reserved2. */
			public Pointer	lpReserved2;

			/** The h std input. */
			public Pointer	hStdInput;

			/** The h std output. */
			public Pointer	hStdOutput;

			/** The h std error. */
			public Pointer	hStdError;
		}

		/** The START f_ usestdhandles. */
		int	STARTF_USESTDHANDLES		= 256;

		/** The IDL e_ priorit y_ class. */
		int	IDLE_PRIORITY_CLASS			= 0x00000040;

		/** The BELO w_ norma l_ priorit y_ class. */
		int	BELOW_NORMAL_PRIORITY_CLASS	= 0x00004000;

		/** The NORMA l_ priorit y_ class. */
		int	NORMAL_PRIORITY_CLASS		= 0x00000020;

		/** The ABOV e_ norma l_ priorit y_ class. */
		int	ABOVE_NORMAL_PRIORITY_CLASS	= 0x00008000;

		/** The HIG h_ priorit y_ class. */
		int	HIGH_PRIORITY_CLASS			= 0x00000080;

		/** The REALTIM e_ priorit y_ class. */
		int	REALTIME_PRIORITY_CLASS		= 0x00000100;

		/*
		 * DWORD WINAPI WaitForSingleObject( HANDLE hHandle, DWORD
		 * dwMilliseconds );
		 */
		/**
		 * Wait for single object.
		 *
		 * @param handle
		 *            the handle
		 * @param dwMilliseconds
		 *            the dw milliseconds
		 *
		 * @return the int
		 */
		int WaitForSingleObject(Pointer handle, int dwMilliseconds);

		/** The INFINITE. */
		int	INFINITE	= 0xFFFFFFFF;

		/*
		 * BOOL WINAPI GetExitCodeProcess( HANDLE hProcess, LPDWORD lpExitCode
		 * );
		 */
		/**
		 * Gets the exit code process.
		 *
		 * @param handle
		 *            the h process
		 * @param lpExitCode
		 *            the lp exit code
		 *
		 * @return true, if successful
		 */
		boolean GetExitCodeProcess(HANDLE handle, IntByReference lpExitCode);

		/** The STIL l_ active. */
		int	STILL_ACTIVE	= 0x103;

		/*
		 * BOOL WINAPI TerminateProcess( HANDLE hProcess, UINT uExitCode );
		 */

		/**
		 * Terminate process.
		 *
		 * @param handle
		 *            the h process
		 * @param uExitCode
		 *            the u exit code
		 *
		 * @return true, if successful
		 */
		boolean TerminateProcess(HANDLE handle, int uExitCode);

		/*
		 * BOOL WINAPI GetProcessAffinityMask( __in HANDLE hProcess, __out
		 * PDWORD_PTR lpProcessAffinityMask, __out PDWORD_PTR
		 * lpSystemAffinityMask );
		 */
		/**
		 * Gets the process affinity mask.
		 *
		 * @param handle
		 *            the h process
		 * @param lpProcessAffinityMask
		 *            the lp process affinity mask
		 * @param lpSystemAffinityMask
		 *            the lp system affinity mask
		 *
		 * @return true, if successful
		 */
		boolean GetProcessAffinityMask(HANDLE handle, IntByReference lpProcessAffinityMask, IntByReference lpSystemAffinityMask);

		/*
		 * BOOL WINAPI SetProcessAffinityMask( __in HANDLE hProcess, __in
		 * DWORD_PTR dwProcessAffinityMask );
		 */
		/**
		 * Sets the process affinity mask.
		 *
		 * @param handle
		 *            the h process
		 * @param dwProcessAffinityMask
		 *            the dw process affinity mask
		 *
		 * @return true, if successful
		 */
		boolean SetProcessAffinityMask(HANDLE handle, int dwProcessAffinityMask);

		/*
		 * BOOL WINAPI CloseHandle( HANDLE hObject );
		 */
		/**
		 * Close handle.
		 *
		 * @param hObject
		 *            the h object
		 *
		 * @return true, if successful
		 */
		public boolean CloseHandle(Pointer hObject);

		/*
		 * HANDLE WINAPI CreateToolhelp32Snapshot( DWORD dwFlags, DWORD
		 * th32ProcessID );
		 */
		/**
		 * Creates the toolhelp32 snapshot.
		 *
		 * @param dwFlags
		 *            the dw flags
		 * @param th32ProcessID
		 *            the th32 process id
		 *
		 * @return the pointer
		 */
		Pointer CreateToolhelp32Snapshot(int dwFlags, int th32ProcessID);

		/** The T h32 c s_ snapprocess. */
		int	TH32CS_SNAPPROCESS	= 0x2;

		int	WAIT_FAILED			= 0xFFFFFFFF;
		int	WAIT_TIMEOUT		= 0x00000102;
		int	WAIT_OBJECT_0		= 0x00000000;
		int	WAIT_ABANDONED		= 0x00000080;

		/*
		 * BOOL WINAPI Process32First( HANDLE hSnapshot, LPPROCESSENTRY32 lppe
		 * );
		 */
		/**
		 * Process32 first.
		 *
		 * @param hSnapshot
		 *            the h snapshot
		 * @param lppe
		 *            the lppe
		 *
		 * @return true, if successful
		 */
		boolean Process32First(Pointer hSnapshot, Structure lppe);

		/*
		 * typedef struct tagPROCESSENTRY32 { DWORD dwSize; DWORD cntUsage;
		 * DWORD th32ProcessID; ULONG_PTR th32DefaultHeapID; DWORD th32ModuleID;
		 * DWORD cntThreads; DWORD th32ParentProcessID; LONG pcPriClassBase;
		 * DWORD dwFlags; TCHAR szExeFile[MAX_PATH]; } PROCESSENTRY32,
		 * PPROCESSENTRY32;
		 */

		/**
		 * The Class PROCESSENTRY32.
		 */
		public static class PROCESSENTRY32 extends Structure
		{

			/** The dw size. */
			public int		dwSize;

			/** The cnt usage. */
			public int		cntUsage;

			/** The th32 process id. */
			public int		th32ProcessID;

			/** The th32 default heap id. */
			public int		th32DefaultHeapID;

			/** The th32 module id. */
			public int		th32ModuleID;

			/** The cnt threads. */
			public int		cntThreads;

			/** The th32 parent process id. */
			public int		th32ParentProcessID;

			/** The pc pri class base. */
			public int		pcPriClassBase;

			/** The dw flags. */
			public int		dwFlags;

			/** The sz exe file. */
			public char[]	szExeFile;
		}

		/** The MA x_ path. */
		int	MAX_PATH	= 260;

		/*
		 * BOOL WINAPI Process32Next( HANDLE hSnapshot, LPPROCESSENTRY32 lppe );
		 */
		/**
		 * Process32 next.
		 *
		 * @param hSnapshot
		 *            the h snapshot
		 * @param lppe
		 *            the lppe
		 *
		 * @return true, if successful
		 */
		boolean Process32Next(Pointer hSnapshot, Structure lppe);

		/*
		 * HANDLE WINAPI OpenProcess( DWORD dwDesiredAccess, BOOL
		 * bInheritHandle, DWORD dwProcessId );
		 */
		/**
		 * Open process.
		 *
		 * @param dwDesiredAccess
		 *            the dw desired access
		 * @param bInheritHandle
		 *            the b inherit handle
		 * @param dwProcessId
		 *            the dw process id
		 *
		 * @return the pointer
		 */
		HANDLE OpenProcess(int dwDesiredAccess, boolean bInheritHandle, int dwProcessId);

		/** The PROCES s_ terminate. */
		int	PROCESS_TERMINATE			= 1;

		/** The PROCES s_ quer y_ information. */
		int	PROCESS_QUERY_INFORMATION	= 1024;

		/** The STANDAR d_ right s_ required. */
		int	STANDARD_RIGHTS_REQUIRED	= 0xF0000;

		/** The SYNCHRONIZE. */
		int	SYNCHRONIZE					= 0x100000;

		/** The PROCES s_ al l_ access. */
		int	PROCESS_ALL_ACCESS			= STANDARD_RIGHTS_REQUIRED | SYNCHRONIZE | 0xFFF;

		/*
		 * BOOL WINAPI GetProcessTimes( __in HANDLE hProcess, __out LPFILETIME
		 * lpCreationTime, __out LPFILETIME lpExitTime, __out LPFILETIME
		 * lpKernelTime, __out LPFILETIME lpUserTime );
		 */
		/**
		 * Gets the process times.
		 *
		 * @param handle
		 *            the h process
		 * @param lpCreationTime
		 *            the lp creation time
		 * @param lpExitTime
		 *            the lp exit time
		 * @param lpKernelTime
		 *            the lp kernel time
		 * @param lpUserTime
		 *            the lp user time
		 *
		 * @return true, if successful
		 */
		boolean GetProcessTimes(HANDLE handle, LongByReference lpCreationTime, LongByReference lpExitTime, LongByReference lpKernelTime,
				LongByReference lpUserTime);

		/*
		 * BOOL WINAPI CreatePipe( __out PHANDLE hReadPipe, __out PHANDLE
		 * hWritePipe, __in LPSECURITY_ATTRIBUTES lpPipeAttributes, __in DWORD
		 * nSize );
		 */
		/**
		 * Creates the pipe.
		 *
		 * @param hReadPipe
		 *            the h read pipe
		 * @param hWritePipe
		 *            the h write pipe
		 * @param lpPipeAttributes
		 *            the lp pipe attributes
		 * @param nSize
		 *            the n size
		 *
		 * @return the int
		 */
		int CreatePipe(PointerByReference hReadPipe, PointerByReference hWritePipe, Structure lpPipeAttributes, int nSize);

		/*
		 * typedef struct _SECURITY_ATTRIBUTES { DWORD nLength; LPVOID
		 * lpSecurityDescriptor; BOOL bInheritHandle; } SECURITY_ATTRIBUTES,
		 * PSECURITY_ATTRIBUTES, LPSECURITY_ATTRIBUTES;
		 */

		/**
		 * The Class SECURITY_ATTRIBUTES.
		 */
		public static class SECURITY_ATTRIBUTES extends Structure
		{

			/** The n length. */
			public int		nLength;

			/** The lp security descriptor. */
			public Pointer	lpSecurityDescriptor;

			/** The b inherit handle. */
			public boolean	bInheritHandle;
		}

		/*
		 * BOOL WINAPI SetHandleInformation( __in HANDLE hObject, __in DWORD
		 * dwMask, __in DWORD dwFlags );
		 */
		/**
		 * Sets the handle information.
		 *
		 * @param hObject
		 *            the h object
		 * @param dwMask
		 *            the dw mask
		 * @param dwFlags
		 *            the dw flags
		 *
		 * @return true, if successful
		 */
		boolean SetHandleInformation(Pointer hObject, int dwMask, int dwFlags);

		/** The HANDL e_ fla g_ inherit. */
		int	HANDLE_FLAG_INHERIT				= 0x00000001;
		int	HANDLE_FLAG_PROTECT_FROM_CLOSE	= 0x00000002;

		/*
		 * HANDLE WINAPI CreateNamedPipe( __in LPCTSTR lpName, __in DWORD
		 * dwOpenMode, __in DWORD dwPipeMode, __in DWORD nMaxInstances, __in
		 * DWORD nOutBufferSize, __in DWORD nInBufferSize, __in DWORD
		 * nDefaultTimeOut, __in_opt LPSECURITY_ATTRIBUTES lpSecurityAttributes
		 * );
		 */
		/**
		 * Creates the named pipe a.
		 *
		 * @param lpName
		 *            the lp name
		 * @param dwOpenMode
		 *            the dw open mode
		 * @param dwPipeMode
		 *            the dw pipe mode
		 * @param nMaxInstances
		 *            the n max instances
		 * @param nOutBufferSize
		 *            the n out buffer size
		 * @param nInBufferSize
		 *            the n in buffer size
		 * @param nDefaultTimeOut
		 *            the n default time out
		 * @param lpSecurityAttributes
		 *            the lp security attributes
		 *
		 * @return the pointer
		 */
		Pointer CreateNamedPipeA(String lpName, int dwOpenMode, int dwPipeMode, int nMaxInstances, int nOutBufferSize, int nInBufferSize,
				int nDefaultTimeOut, SECURITY_ATTRIBUTES lpSecurityAttributes);

		/** The PIP e_ acces s_ outbound. */
		int	PIPE_ACCESS_OUTBOUND	= 0x00000002;

		/** The PIP e_ acces s_ inbound. */
		int	PIPE_ACCESS_INBOUND		= 0x00000001;

		/** The PIP e_ wait. */
		int	PIPE_WAIT				= 0x00000000;

		/** The PIP e_ nowait. */
		int	PIPE_NOWAIT				= 0x00000001;

		/** The GENERI c_ read. */
		int	GENERIC_READ			= 0x80000000;

		/**
		 * Creates the file a.
		 *
		 * @param lpFileName
		 *            the lp file name
		 * @param dwDesiredAccess
		 *            the dw desired access
		 * @param dwShareMode
		 *            the dw share mode
		 * @param lpSecurityAttributes
		 *            the lp security attributes
		 * @param dwCreationDisposition
		 *            the dw creation disposition
		 * @param dwFlagsAndAttributes
		 *            the dw flags and attributes
		 * @param hTemplateFile
		 *            the h template file
		 *
		 * @return the pointer
		 */
		Pointer CreateFileA(String lpFileName, int dwDesiredAccess, int dwShareMode, SECURITY_ATTRIBUTES lpSecurityAttributes,
				int dwCreationDisposition, int dwFlagsAndAttributes, Pointer hTemplateFile);

		/*
		 * BOOL WINAPI ConnectNamedPipe( __in HANDLE hNamedPipe, __inout_opt
		 * LPOVERLAPPED lpOverlapped );
		 */
		/**
		 * Connect named pipe.
		 *
		 * @param hNamedPipe
		 *            the h named pipe
		 * @param lpOverlapped
		 *            the lp overlapped
		 *
		 * @return true, if successful
		 */
		boolean ConnectNamedPipe(Pointer hNamedPipe, PointerByReference lpOverlapped);

		/** The INVALI d_ handl e_ value. */
		Pointer	INVALID_HANDLE_VALUE	= Pointer.createConstant(-1);

		/*
		 * BOOL WINAPI WaitNamedPipe( __in LPCTSTR lpNamedPipeName, __in DWORD
		 * nTimeOut );
		 */
		/**
		 * Wait named pipe a.
		 *
		 * @param lpNamedPipeName
		 *            the lp named pipe name
		 * @param nTimeOut
		 *            the n time out
		 *
		 * @return true, if successful
		 */
		boolean WaitNamedPipeA(String lpNamedPipeName, int nTimeOut);

		/** The NMPWAI t_ us e_ defaul t_ wait. */
		int	NMPWAIT_USE_DEFAULT_WAIT	= 0;

		/** The NMPWAI t_ wai t_ forever. */
		int	NMPWAIT_WAIT_FOREVER		= 0xffffffff;

		/*
		 * BOOL WINAPI SetCurrentDirectory( __in LPCTSTR lpPathName );
		 */
		boolean SetCurrentDirectoryA(String lpPathName);

		/*
		 * typedef struct _MEMORY_BASIC_INFORMATION { PVOID BaseAddress; PVOID
		 * AllocationBase; DWORD AllocationProtect; SIZE_T RegionSize; DWORD
		 * State; DWORD Protect; DWORD Type; } MEMORY_BASIC_INFORMATION,
		 * *PMEMORY_BASIC_INFORMATION;
		 */
		public static class MEMORY_BASIC_INFORMATION extends Structure
		{
			public Pointer	BaseAddress;
			public Pointer	AllocationBase;
			public int		AllocationProtect;
			public int		RegionSize;
			public int		State;
			public int		Protect;
			public int		Type;
		}

		public static int	PAGE_NOACCESS	= 0x01;
		public static int	PAGE_EXECUTE	= 0x10;

		/*
		 * SIZE_T WINAPI VirtualQueryEx( __in HANDLE hProcess, __in_opt LPCVOID
		 * lpAddress, __out PMEMORY_BASIC_INFORMATION lpBuffer, __in SIZE_T
		 * dwLength );
		 */
		int VirtualQueryEx(Pointer hProcess, Pointer lpAddress, Pointer lpBuffer, int dwLength);

	}

	/**
	 * The Interface Ntdll.
	 */
	public interface Ntdll extends com.sun.jna.win32.StdCallLibrary
	{

		/** The INSTANCE. */
		Ntdll	INSTANCE	= (Ntdll) Native.loadLibrary("Ntdll", Ntdll.class);

		/*
		 * NTOSAPI NTSTATUS NTAPI ZwReadVirtualMemory( /IN/ HANDLE
		 * ProcessHandle, /IN/ PVOID BaseAddress, /OUT/ PVOID Buffer, /IN/ ULONG
		 * BufferLength, /OUT/ PULONG ReturnLength /OPTIONAL/);
		 */

		/**
		 * Zw read virtual memory.
		 *
		 * @param ProcessHandle
		 *            the process handle
		 * @param BaseAddress
		 *            the base address
		 * @param Buffer
		 *            the buffer
		 * @param BufferLength
		 *            the buffer length
		 * @param ReturnLength
		 *            the return length
		 *
		 * @return the int
		 */
		int ZwReadVirtualMemory(Pointer ProcessHandle, Pointer BaseAddress, Pointer Buffer, int BufferLength, IntByReference ReturnLength);

		/*
		 * NTSTATUS WINAPI ZwQueryInformationProcess( __in HANDLE ProcessHandle,
		 * __in PROCESSINFOCLASS ProcessInformationClass, __out PVOID
		 * ProcessInformation, __in ULONG ProcessInformationLength, __out_opt
		 * PULONG ReturnLength );
		 */
		/**
		 * Zw query information process.
		 *
		 * @param process
		 *            the process handle
		 * @param ProcessInformationClass
		 *            the process information class
		 * @param ProcessInformation
		 *            the process information
		 * @param ProcessInformationLength
		 *            the process information length
		 * @param ReturnLength
		 *            the return length
		 *
		 * @return the int
		 */
		int ZwQueryInformationProcess(HANDLE process, int ProcessInformationClass, Pointer ProcessInformation, int ProcessInformationLength,
				IntByReference ReturnLength);

		/*
		 * typedef struct _PROCESS_BASIC_INFORMATION { PVOID Reserved1; PPEB
		 * PebBaseAddress; PVOID Reserved2[2]; ULONG_PTR UniqueProcessId; PVOID
		 * Reserved3; } PROCESS_BASIC_INFORMATION;
		 */
		/**
		 * The Class PROCESS_BASIC_INFORMATION.
		 */
		class PROCESS_BASIC_INFORMATION extends Structure
		{

			/** The Reserved1. */
			public Pointer	Reserved1;

			/** The Peb base address. */
			public Pointer	PebBaseAddress;

			/** The Reserved2. */
			public int[]	Reserved2	= new int[2];

			/** The Unique process id. */
			public Pointer	UniqueProcessId;

			/** The Reserved3. */
			public Pointer	Reserved3;
		}

		/*
		 * typedef struct _PEB { BYTE Reserved1[2]; BYTE BeingDebugged; BYTE
		 * Reserved2[1]; PVOID Reserved3[2]; PPEB_LDR_DATA Ldr;
		 * PRTL_USER_PROCESS_PARAMETERS ProcessParameters; BYTE Reserved4[104];
		 * PVOID Reserved5[52]; PPS_POST_PROCESS_INIT_ROUTINE
		 * PostProcessInitRoutine; BYTE Reserved6[128]; PVOID Reserved7[1];
		 * ULONG SessionId; } PEB, PPEB;
		 */
		/**
		 * The Class PEB.
		 */
		class PEB extends Structure
		{

			/** The Reserved1. */
			public byte[]	Reserved1	= new byte[2];

			/** The Being debugged. */
			public byte		BeingDebugged;

			/** The Reserved2. */
			public byte		Reserved2;

			/** The Reserved3. */
			public int[]	Reserved3	= new int[2];

			/** The Ldr. */
			public Pointer	Ldr;

			/** The Process parameters. */
			public Pointer	ProcessParameters;

			/** The Reserved4. */
			public byte[]	Reserved4	= new byte[104];

			/** The Reserved5. */
			public int[]	Reserved5	= new int[52];

			/** The Post process init routine. */
			public Pointer	PostProcessInitRoutine;

			/** The Reserved6. */
			public byte[]	Reserved6	= new byte[128];

			/** The Reserved7. */
			public int[]	Reserved7	= new int[1];

			/** The Session id. */
			public int		SessionId;

		}

		/*
		 * typedef struct _PEB { BYTE Reserved1[2]; BYTE BeingDebugged; BYTE
		 * Reserved2[21]; PPEB_LDR_DATA LoaderData; PRTL_USER_PROCESS_PARAMETERS
		 * ProcessParameters; BYTE Reserved3[520]; PPS_POST_PROCESS_INIT_ROUTINE
		 * PostProcessInitRoutine; BYTE Reserved4[136]; ULONG SessionId; } PEB;
		 */
		class PEB64 extends Structure
		{
			public byte[]	Reserved1	= new byte[2];
			public byte		BeingDebugged;
			public byte[]	Reserved2	= new byte[21];		;
			public Pointer	Ldr;
			public Pointer	ProcessParameters;
			public byte[]	Reserved3	= new byte[520];
			public Pointer	PostProcessInitRoutine;
			public byte[]	Reserved4	= new byte[136];
			public int		SessionId;
		}

		/*
		 * typedef struct _RTL_USER_PROCESS_PARAMETERS { BYTE Reserved1[16]; 16
		 * PVOID Reserved2[10]; 40 UNICODE_STRING ImagePathName; UNICODE_STRING
		 * CommandLine; } RTL_USER_PROCESS_PARAMETERS,
		 * PRTL_USER_PROCESS_PARAMETERS;
		 *
		 * typedef struct _RTL_USER_PROCESS_PARAMETERS { ULONG MaximumLength; 4
		 * ULONG Length; 8 ULONG Flags; 12 ULONG DebugFlags; 16 PVOID
		 * ConsoleHandle; 4 ULONG ConsoleFlags; 8 HANDLE StdInputHandle; 12
		 * HANDLE StdOutputHandle; 16 HANDLE StdErrorHandle; 20 UNICODE_STRING
		 * CurrentDirectoryPath; 28 HANDLE CurrentDirectoryHandle; 32
		 * UNICODE_STRING DllPath; 40 UNICODE_STRING ImagePathName; 48
		 * UNICODE_STRING CommandLine; 56 PVOID Environment; 4 ULONG
		 * StartingPositionLeft; 8 ULONG StartingPositionTop; 12 ULONG Width; 16
		 * ULONG Height; 20 ULONG CharWidth; 24 ULONG CharHeight; 28 ULONG
		 * ConsoleTextAttributes; 32 ULONG WindowFlags; 36 ULONG
		 * ShowWindowFlags; 40 UNICODE_STRING WindowTitle; 48 UNICODE_STRING
		 * DesktopName; UNICODE_STRING ShellInfo; UNICODE_STRING RuntimeData;
		 * RTL_DRIVE_LETTER_CURDIR DLCurrentDirectory[0x20]; }
		 * RTL_USER_PROCESS_PARAMETERS,PRTL_USER_PROCESS_PARAMETERS;
		 */

		/**
		 * The Class RTL_USER_PROCESS_PARAMETERS.
		 */
		class RTL_USER_PROCESS_PARAMETERS extends Structure
		{

			/** The Reserved1. */
			public byte[]			Reserved1	= new byte[16];

			/** The Reserved2. */
			public int[]			Reserved2	= new int[5];
			// public int[] Reserved2 = new int[16];

			public UNICODE_STRING	CurrentDirectoryPath;
			public Pointer			CurrentDirectoryHandle;
			public UNICODE_STRING	DllPath;

			/** The Image path name. */
			public UNICODE_STRING	ImagePathName;

			/** The Command line. */
			public UNICODE_STRING	CommandLine;
			public Pointer			Environment;				// new

			public int[]			reserved3	= new int[9];	// int[10];

			public UNICODE_STRING	WindowTitle;

		}

		/*
		 * typedef struct _LSA_UNICODE_STRING { USHORT Length; USHORT
		 * MaximumLength; PWSTR Buffer; } LSA_UNICODE_STRING,
		 * PLSA_UNICODE_STRING, UNICODE_STRING, PUNICODE_STRING;
		 */
		/**
		 * The Class UNICODE_STRING.
		 */
		class UNICODE_STRING extends Structure
		{

			/** The Length. */
			public short	Length	= 0;

			/** The Maximum length. */
			public short	MaximumLength;

			/** The Buffer. */
			public Pointer	Buffer;
		}
	}

	public interface MyAdvapi extends Advapi32
	{
		MyAdvapi	INSTANCE	= (MyAdvapi) Native.loadLibrary("Advapi32", MyAdvapi.class, Options.UNICODE_OPTIONS);

		/*
		 * BOOL WINAPI LookupAccountSid( __in_opt LPCTSTR lpSystemName, __in
		 * PSID lpSid, __out_opt LPTSTR lpName, __inout LPDWORD cchName,
		 * __out_opt LPTSTR lpReferencedDomainName, __inout LPDWORD
		 * cchReferencedDomainName, __out PSID_NAME_USE peUse );
		 */
		boolean LookupAccountSidW(String lpSystemName, Pointer lpSid, Memory lpName, IntByReference cchName, Memory lpReferencedDomainName,
				IntByReference cchReferencedDomainName, IntByReference peUse);

		/*
		 * typedef struct _SID_AND_ATTRIBUTES { PSID Sid; DWORD Attributes; }
		 * SID_AND_ATTRIBUTES,PSID_AND_ATTRIBUTES;
		 */
		static class SID_AND_ATTRIBUTES extends Structure
		{
			public Pointer	Sid;
			public int		Attributes;
		}

		/*
		 * typedef struct _TOKEN_USER { SID_AND_ATTRIBUTES User; } TOKEN_USER,
		 * PTOKEN_USER;
		 */
		static class TOKEN_USER extends Structure
		{
			public SID_AND_ATTRIBUTES	User;

			public TOKEN_USER(Pointer p)
			{
				super();
				this.useMemory(p);
				this.read();
			}
		}

		public static final int	TokenPrivileges	= 3;
		public static final int	TokenUser		= 1;

		/*
		 * BOOL WINAPI GetTokenInformation( __in HANDLE TokenHandle, __in
		 * TOKEN_INFORMATION_CLASS TokenInformationClass, __out_opt LPVOID
		 * TokenInformation, __in DWORD TokenInformationLength, __out PDWORD
		 * ReturnLength );
		 */
		boolean GetTokenInformation(Pointer TokenHandle, int TokenInformationClass, Memory TokenInformation, int TokenInformationLength,
				IntByReference ReturnLength);

		/*
		 * BOOL WINAPI InitializeSecurityDescriptor( __out PSECURITY_DESCRIPTOR
		 * pSecurityDescriptor, __in DWORD dwRevision );
		 */
		boolean InitializeSecurityDescriptor(Memory pSecurityDescriptor, int dwRevision);

		public static final int	SECURITY_DESCRIPTOR_MIN_LENGTH	= 20;
		public static final int	SECURITY_DESCRIPTOR_REVISION	= 1;

		/*
		 * BOOL WINAPI SetSecurityDescriptorSacl( __inout PSECURITY_DESCRIPTOR
		 * pSecurityDescriptor, __in BOOL bSaclPresent, __in_opt PACL pSacl,
		 * __in BOOL bSaclDefaulted );
		 */
		boolean SetSecurityDescriptorDacl(Pointer pSecurityDescriptor, boolean bSaclPresent, Pointer pSacl, boolean bSaclDefaulted);

		public static int	SE_PRIVILEGE_ENABLED	= 2;

		/*
		 * typedef struct _LUID { DWORD LowPart; LONG HighPart; } LUID,PLUID;
		 */
		static class LUID extends Structure
		{
			public int	LowPart;
			public int	HighPart;
		}

		/*
		 * typedef struct _LUID_AND_ATTRIBUTES { LUID Luid; DWORD Attributes; }
		 * LUID_AND_ATTRIBUTES,PLUID_AND_ATTRIBUTES;
		 */
		static class LUID_AND_ATTRIBUTES extends Structure
		{
			public LUID	Luid;
			public int	Attributes;
		}

		/*
		 * typedef struct _TOKEN_PRIVILEGES { DWORD PrivilegeCount;
		 * LUID_AND_ATTRIBUTES Privileges[ANYSIZE_ARRAY]; } TOKEN_PRIVILEGES,
		 * PTOKEN_PRIVILEGES;
		 */
		static class TOKEN_PRIVILEGES extends Structure
		{
			public int						PrivilegeCount	= 1;
			public LUID_AND_ATTRIBUTES[]	Privileges		= new LUID_AND_ATTRIBUTES[1];

			public TOKEN_PRIVILEGES()
			{
				super();
				Privileges[0] = new LUID_AND_ATTRIBUTES();
			}

			public TOKEN_PRIVILEGES(Pointer p)
			{
				super();
				PrivilegeCount = p.getInt(0);
				Privileges = new LUID_AND_ATTRIBUTES[PrivilegeCount];
				this.useMemory(p);
				this.read();
			}

		}

		/*
		 * BOOL WINAPI AdjustTokenPrivileges( __in HANDLE TokenHandle, __in BOOL
		 * DisableAllPrivileges, __in_opt PTOKEN_PRIVILEGES NewState, __in DWORD
		 * BufferLength, __out_opt PTOKEN_PRIVILEGES PreviousState, __out_opt
		 * PDWORD ReturnLength );
		 */
		boolean AdjustTokenPrivileges(Pointer TokenHandle, boolean DisableAllPrivileges, TOKEN_PRIVILEGES NewState, int BufferLength,
				PointerByReference PreviousState, IntByReference ReturnLength);

		/*
		 * BOOL WINAPI LookupPrivilegeValue( __in_opt LPCTSTR lpSystemName, __in
		 * LPCTSTR lpName, __out PLUID lpLuid );
		 */
		boolean LookupPrivilegeValueA(String lpSystemName, String lpName, LUID lpLuid);

		public static final String	SE_ASSIGNPRIMARYTOKEN_NAME	= "SeAssignPrimaryTokenPrivilege";
		public static final String	SE_INCREASE_QUOTA_NAME		= "SeIncreaseQuotaPrivilege";
		public static final String	SE_DEBUG_NAME				= "SeDebugPrivilege";
		public static final String	SE_TCB_NAME					= "SeTcbPrivilege";

		/*
		 * BOOL WINAPI OpenProcessToken( __in HANDLE ProcessHandle, __in DWORD
		 * DesiredAccess, __out PHANDLE TokenHandle );
		 */
		boolean OpenProcessToken(HANDLE ProcessHandle, int DesiredAccess, PointerByReference TokenHandle);

		public static final int	STANDARD_RIGHTS_READ	= 0x20000;
		public static final int	STANDARD_RIGHTS_WRITE	= 0x20000;
		public static final int	TOKEN_QUERY				= 0x0008;
		public static final int	TOKEN_ADJUST_PRIVILEGES	= 0x0020;
		public static final int	TOKEN_ADJUST_GROUPS		= 0x0040;
		public static final int	TOKEN_ADJUST_DEFAULT	= 0x0080;
		public static final int	TOKEN_DUPLICATE			= 0x0002;
		public static final int	TOKEN_IMPERSONATE		= 0x0004;

		public static final int	TOKEN_READ				= STANDARD_RIGHTS_READ | TOKEN_QUERY;

		public static final int	TOKEN_WRITE				= STANDARD_RIGHTS_WRITE | TOKEN_ADJUST_PRIVILEGES | TOKEN_ADJUST_GROUPS
																| TOKEN_ADJUST_DEFAULT;

		/*
		 * BOOL WINAPI CreateProcessWithLogonW( __in LPCWSTR lpUsername,
		 * __in_opt LPCWSTR lpDomain, __in LPCWSTR lpPassword, __in DWORD
		 * dwLogonFlags, __in_opt LPCWSTR lpApplicationName, __inout_opt LPWSTR
		 * lpCommandLine, __in DWORD dwCreationFlags, __in_opt LPVOID
		 * lpEnvironment, __in_opt LPCWSTR lpCurrentDirectory, __in
		 * LPSTARTUPINFOW lpStartupInfo, __out LPPROCESS_INFORMATION
		 * lpProcessInfo );
		 */
		boolean CreateProcessWithLogonW(WString lpUsername, WString lpDomain, WString lpPassword, int dwLogonFlags, WString lpApplicationName,
				WString lpCommandLine, int dwCreationFlags, Pointer lpEnvironment, WString lpCurrentDirectory, Structure lpStartupInfo,
				Structure lpProcessInfo);

		public static final int	LOGON_WITH_PROFILE			= 0x00000001;
		public static final int	LOGON_NETCREDENTIALS_ONLY	= 0x00000002;

		/*
		 * BOOL LogonUser( __in LPTSTR lpszUsername, __in_opt LPTSTR lpszDomain,
		 * __in LPTSTR lpszPassword, __in DWORD dwLogonType, __in DWORD
		 * dwLogonProvider, __out PHANDLE phToken );
		 */
		boolean LogonUserA(String lpszUsername, String lpszDomain, String lpszPassword, int dwLogonType, int dwLogonProvider,
				PointerByReference phToken);

		boolean LogonUserW(WString lpszUsername, WString lpszDomain, WString lpszPassword, int dwLogonType, int dwLogonProvider,
				PointerByReference phToken);

		public static final int	LOGON32_LOGON_INTERACTIVE		= 2;
		public static final int	LOGON32_LOGON_NETWORK			= 3;
		public static final int	LOGON32_LOGON_BATCH				= 4;
		public static final int	LOGON32_LOGON_SERVICE			= 5;
		public static final int	LOGON32_LOGON_UNLOCK			= 7;
		public static final int	LOGON32_LOGON_NETWORK_CLEARTEXT	= 8;
		public static final int	LOGON32_LOGON_NEW_CREDENTIALS	= 9;

		public static final int	LOGON32_PROVIDER_DEFAULT		= 0;
		public static final int	LOGON32_PROVIDER_WINNT35		= 1;
		public static final int	LOGON32_PROVIDER_WINNT40		= 2;
		public static final int	LOGON32_PROVIDER_WINNT50		= 3;

		/*
		 * BOOL WINAPI ImpersonateLoggedOnUser( __in HANDLE hToken );
		 */
		boolean ImpersonateLoggedOnUser(Pointer hToken);

		/*
		 * BOOL WINAPI CreateProcessAsUser( __in_opt HANDLE hToken, __in_opt
		 * LPCTSTR lpApplicationName, __inout_opt LPTSTR lpCommandLine, __in_opt
		 * LPSECURITY_ATTRIBUTES lpProcessAttributes, __in_opt
		 * LPSECURITY_ATTRIBUTES lpThreadAttributes, __in BOOL bInheritHandles,
		 * __in DWORD dwCreationFlags, __in_opt LPVOID lpEnvironment, __in_opt
		 * LPCTSTR lpCurrentDirectory, __in LPSTARTUPINFO lpStartupInfo, __out
		 * LPPROCESS_INFORMATION lpProcessInformation );
		 */
		boolean CreateProcessAsUserW(Pointer hToken, WString lpApplicationName, WString lpCommandLine, Structure lpProcessAttributes,
				Structure lpThreadAttributes, boolean bInheritHandles, int dwCreationFlags, Structure lpEnvironment, WString lpCurrentDirectory,
				Structure lpStartupInfo, Structure lpProcessInformation);

		static class SECURITY_ATTRIBUTES
		{
			public int		nLength;
			public Pointer	lpSecurityDescriptor;
			boolean			bInheritHandle;
		}

	}

	public interface Secur32 extends StdCallLibrary
	{

		/** The INSTANCE. */
		Secur32	INSTANCE	= (Secur32) Native.loadLibrary("Secur32", Secur32.class);

		/*
		 * BOOLEAN WINAPI GetUserNameEx( __in EXTENDED_NAME_FORMAT NameFormat,
		 * __out LPTSTR lpNameBuffer, __inout PULONG lpnSize );
		 */
		boolean GetUserNameEx(int NameFormat, Memory lpNameBuffer, IntByReference lpnSize);

	}

	/** The _startup info. */
    MyKernel32.STARTUPINFO _startupInfo;

	/** The _process information. */
    volatile MyKernel32.PROCESS_INFORMATION _processInformation;

	/** The in read. */
	PointerByReference				inRead		= null;

	/** The in write. */
	PointerByReference				inWrite		= null;

	/** The out read. */
	PointerByReference				outRead		= null;

	/** The out write. */
	PointerByReference				outWrite	= null;

	/** The err read. */
	PointerByReference				errRead		= null;

	/** The err write. */
	PointerByReference				errWrite	= null;

	/** The sa. */
    MyKernel32.SECURITY_ATTRIBUTES sa;

	/** The m_h out pipe. */
	Pointer							m_hOutPipe	= null;

	/** The m_h err pipe. */
	Pointer							m_hErrPipe	= null;

	/** The m_h in pipe. */
	Pointer							m_hInPipe	= null;

	/** The in write pipe. */
	Pointer							inWritePipe	= null;

	/** The out read pipe. */
	Pointer							outReadPipe	= null;

	/** The err read pipe. */
	Pointer							errReadPipe	= null;

	/**
	 * Gets the process.
	 *
	 * @param pid
	 *            the pid
	 *
	 * @return the process
	 */
	public static Process getProcess(int pid)
	{
		WindowsXPProcess result = new WindowsXPProcess();
		HANDLE hProcess = MyKernel32.INSTANCE.OpenProcess(MyKernel32.PROCESS_ALL_ACCESS, false, pid);
		if (hProcess == null)
			hProcess = MyKernel32.INSTANCE.OpenProcess(MyKernel32.PROCESS_QUERY_INFORMATION, false, pid);
		if (hProcess == null)
			return null;

		result._pid = pid;
        result._processInformation = new MyKernel32.PROCESS_INFORMATION();
		result._processInformation.dwProcessId = pid;
		result._processInformation.hProcess = hProcess;
		result._cmd = result.getCommandLineInternal();
		// this does not always work (why ??), if so try again, then this
		// normally does
		// on win64 PEB of 64 bit cannot be accessed from wow -> use wmi
		if (result._cmd.equals("?"))
			result._cmd = result.getCommandLineInternalWMI();
		if ("?".equals(result._cmd))
		{
			System.err.println("Could not get commandline");
		}
		else
			System.out.println("Command line of " + pid + ": " + result._cmd);
		PointerByReference hToken = new PointerByReference();
		HANDLE hp = new HANDLE();
		hp.setPointer(hProcess.getPointer());
		if (MyAdvapi.INSTANCE.OpenProcessToken(hp, MyAdvapi.TOKEN_READ, hToken))
		{
			IntByReference dwSize = new IntByReference();
			MyAdvapi.INSTANCE.GetTokenInformation(hToken.getValue(), MyAdvapi.TokenUser, null, 0, dwSize);
			{
				Memory pTokenUser = new Memory(dwSize.getValue());
				if (MyAdvapi.INSTANCE.GetTokenInformation(hToken.getValue(), MyAdvapi.TokenUser, pTokenUser, dwSize.getValue(), dwSize))
				{
					MyAdvapi.TOKEN_USER tokenUser = new MyAdvapi.TOKEN_USER(pTokenUser);
					Pointer lpSid = tokenUser.User.Sid;
					Memory lpName = new Memory(256);
					IntByReference cchName = new IntByReference();
					cchName.setValue(256);
					Memory lpReferencedDomainName = new Memory(256);
					IntByReference cchReferencedDomainName = new IntByReference();
					cchReferencedDomainName.setValue(256);
					IntByReference peUse = new IntByReference();
					if (MyAdvapi.INSTANCE.LookupAccountSidW(null, lpSid, lpName, cchName, lpReferencedDomainName, cchReferencedDomainName, peUse))

						result._user = lpReferencedDomainName.getString(0, true) + "\\" + lpName.getString(0, true);
					;
					// System.out.println(result._user);
				}
			}
			if (result._user == null)
				System.out.println("could not get user name OS error #" + MyKernel32.INSTANCE.GetLastError());
			MyKernel32.INSTANCE.CloseHandle(hToken.getValue());
		}
		return result;
	}

	private boolean setPrivilege(Pointer hToken, String lpszPrivilege, boolean bEnablePrivilege)
	{
        MyAdvapi.TOKEN_PRIVILEGES tp = new MyAdvapi.TOKEN_PRIVILEGES();
		MyAdvapi.LUID luid = new MyAdvapi.LUID();
		luid.size();

		if (!MyAdvapi.INSTANCE.LookupPrivilegeValueA(null, lpszPrivilege, luid))
			return false;

		tp.Privileges[0].Luid = luid;
		tp.write();

		if (bEnablePrivilege)
			tp.Privileges[0].Attributes = MyAdvapi.SE_PRIVILEGE_ENABLED;
		else
			tp.Privileges[0].Attributes = 0;

		int size = tp.size();
		boolean result = MyAdvapi.INSTANCE.AdjustTokenPrivileges(hToken, false, tp, 0, null, null);
		// return GetLastError() == ERROR_SUCCESS;
		if (!result)
		{
			int errNr = MyKernel32.INSTANCE.GetLastError();
			log("error setting privliges OS error #" + errNr + "/" + Integer.toHexString(errNr));
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.rzo.yajsw.os.Process#start()
	 */
	public boolean start()
	{
		boolean result = false;

		if (isRunning())
		{
			log("process already running -> abort start");
			return false;
		}
		else
		{
			setPid(-1);
			setExitCode(-1);
		}
		_started = false;

		int PIPE_SIZE = 1024; // buffer size for pipes
		int PIPE_TIMEOUT = 12000; // time to wait for pipe

		if (_arrCmd == null && _cmd == null)
			return false;
		if (_cmd == null)
		{
			_cmd = "";
			for (String cmd : _arrCmd)
				if (cmd.startsWith("\""))
					_cmd += cmd + " ";
				else
					_cmd += '"' + cmd + "\" ";
			// _cmd += cmd + " ";
		}
		if (_debug)
			log("exec: " + _cmd);
		if (_processInformation != null)
		{
			log("process not correctly disposed -> abort start");
			return false;
		}
		try
		{
			destroyed = false;
            _startupInfo = new MyKernel32.STARTUPINFO();
			_startupInfo.clear();
            _processInformation = new MyKernel32.PROCESS_INFORMATION();
			_processInformation.clear();
			if (_pipeStreams)
			{
				if (sa == null)
				{
                    sa = new MyKernel32.SECURITY_ATTRIBUTES();
					sa.clear();
					sa.nLength = sa.size();
					sa.lpSecurityDescriptor = null;
					sa.bInheritHandle = true;// 1; // true otherwise streams are
					// not piped
				}
				inRead = new PointerByReference();
				inWrite = new PointerByReference();
				outRead = new PointerByReference();
				outWrite = new PointerByReference();
				errRead = new PointerByReference();
				errWrite = new PointerByReference();

				if (MyKernel32.INSTANCE.CreatePipe(inRead, inWrite, sa, 0) == 0 || MyKernel32.INSTANCE.CreatePipe(outRead, outWrite, sa, 0) == 0
						|| MyKernel32.INSTANCE.CreatePipe(errRead, errWrite, sa, 0) == 0)
				{
					log("Error in CreatePipe " + Integer.toHexString(MyKernel32.INSTANCE.GetLastError()));
					return false;
				}

				_startupInfo.dwFlags = MyKernel32.STARTF_USESTDHANDLES;
				_startupInfo.hStdInput = inRead.getValue();
				_startupInfo.hStdOutput = outWrite.getValue();
				_startupInfo.hStdError = errWrite.getValue();

				if (!MyKernel32.INSTANCE.SetHandleInformation(inWrite.getValue(), MyKernel32.HANDLE_FLAG_INHERIT, 0)
						|| !MyKernel32.INSTANCE.SetHandleInformation(outRead.getValue(), MyKernel32.HANDLE_FLAG_INHERIT, 0)
						|| !MyKernel32.INSTANCE.SetHandleInformation(errRead.getValue(), MyKernel32.HANDLE_FLAG_INHERIT, 0)
						// for some unknown reason: if we add the following
						// lines we do not get "operation on non socket" error
						// in mina
						|| !MyKernel32.INSTANCE.SetHandleInformation(inWrite.getValue(), MyKernel32.HANDLE_FLAG_PROTECT_FROM_CLOSE,
								MyKernel32.HANDLE_FLAG_PROTECT_FROM_CLOSE)
						|| !MyKernel32.INSTANCE.SetHandleInformation(outRead.getValue(), MyKernel32.HANDLE_FLAG_PROTECT_FROM_CLOSE,
								MyKernel32.HANDLE_FLAG_PROTECT_FROM_CLOSE)
						|| !MyKernel32.INSTANCE.SetHandleInformation(errRead.getValue(), MyKernel32.HANDLE_FLAG_PROTECT_FROM_CLOSE,
								MyKernel32.HANDLE_FLAG_PROTECT_FROM_CLOSE))

				{
					log("error in set handle -> abort start");
					return false;
				}
				if (this._redirectErrorStream)
					MyKernel32.INSTANCE.SetHandleInformation(errRead.getValue(), MyKernel32.HANDLE_FLAG_INHERIT, 0);

			}

			int creationFlag = 0;
			if (!_visible)
			{
				creationFlag |= MyKernel32.CREATE_NO_WINDOW | MyKernel32.CREATE_UNICODE_ENVIRONMENT;
				_startupInfo.lpTitle = null;
			}
			else
			{
				creationFlag |= MyKernel32.CREATE_NEW_CONSOLE | MyKernel32.CREATE_UNICODE_ENVIRONMENT;
				_startupInfo.lpTitle = new WString(_title);
			}

			creationFlag |= getPriorityFlag();

			// do not inherit handles. otherwise resources are not freed if
			// parent is killed
			// inherit only when we need to pipe the streams
			_startupInfo.write();
			WString cmd = new WString(_cmd);
			WString wDir = getWorkingDir() == null ? null : new WString(getWorkingDir());
			String stdUser = standardizeUser(_user);
            StringBlock environment = null;
			WString[] env = null;
			if (_environment.size() != 0)
			{
				env = new WString[_environment.size()];
				int i = 0;
				for (String[] entry : _environment)
				{
					env[i++] = new WString(entry[0] + "=" + entry[1]);
				}
                environment = new StringBlock( env );
			}
			if (stdUser == null || stdUser.equals(currentUser()))
			{
				result = MyKernel32.INSTANCE.CreateProcessW(null, cmd, null, null, _pipeStreams, creationFlag, environment, wDir, _startupInfo,
						_processInformation);
			}
			else
			{
				WString user = null;
				;
				WString domain = null;
				;
				int i = _user.lastIndexOf("\\");
				if (i > 0)
				{
					user = new WString(_user.substring(_user.lastIndexOf("\\") + 1));
					domain = new WString(_user.substring(0, _user.lastIndexOf("\\")));
				}
				else
					user = new WString(_user);
				WString password = null;
				if (_password != null)
					password = new WString(_password);

				log("current user :: requested user: " + currentUserName() + " :: " + stdUser);
				// in windows 2008: system user seems to be <computername>$
				// could not find documentation on this.
				if (!("SYSTEM".equals(currentUserName()) || currentUserName().endsWith("$")))
				{
					// createProcessWithLogon : cmd line is only 1024 char long
					// parent process is not current process.
					// -> use CreateProcessAsUser
					// result = MyAdvapi.INSTANCE.CreateProcessWithLogonW(user,
					// domain, password, MyAdvapi.LOGON_WITH_PROFILE, null, cmd,
					// creationFlag, null, wDir, _startupInfo,
					// _processInformation);

					/**/
					PointerByReference phToken = new PointerByReference();

					String stUser = user.toString();
					String stDomain = domain == null ? null : domain.toString();
					String stPassword = password == null ? "" : password.toString();
					result = true;
					// result = MyAdvapi.INSTANCE.LogonUserA(stUser, stDomain,
					// stPassword, MyAdvapi.LOGON32_LOGON_NEW_CREDENTIALS,
					// MyAdvapi.LOGON32_PROVIDER_WINNT50, phToken);
					if (result)
					{
						// HANDLE hCurrentProcess =
						// MyKernel32.INSTANCE.GetCurrentProcess();
						// PointerByReference hTokenSelf = new
						// PointerByReference();
						// result =
						// MyAdvapi.INSTANCE.OpenProcessToken(hCurrentProcess,
						// MyAdvapi.TOKEN_READ | MyAdvapi.TOKEN_WRITE |
						// MyAdvapi.TOKEN_DUPLICATE |
						// MyAdvapi.TOKEN_IMPERSONATE, hTokenSelf );
						/*
						 * Memory pSD = new
						 * Memory(MyAdvapi.SECURITY_DESCRIPTOR_MIN_LENGTH);
						 * pSD.clear(); if (result) result =
						 * MyAdvapi.INSTANCE.InitializeSecurityDescriptor(pSD,
						 * MyAdvapi.SECURITY_DESCRIPTOR_REVISION);
						 *
						 * if (result) result =
						 * MyAdvapi.INSTANCE.SetSecurityDescriptorDacl(pSD,
						 * true, null, false);
						 */
						if (result)
						{

							/*
							 * SECURITY_ATTRIBUTES sap = new
							 * SECURITY_ATTRIBUTES(); sap.clear(); sap.nLength =
							 * sap.size(); sap.lpSecurityDescriptor = pSD;
							 * sap.bInheritHandle = false;
							 */
							// result =
							// MyAdvapi.INSTANCE.ImpersonateLoggedOnUser(phToken.getValue());
							/**/// System.out.println(MyAdvapi.SE_ASSIGNPRIMARYTOKEN_NAME+" "+this.doesUserHavePrivilege(MyAdvapi.SE_ASSIGNPRIMARYTOKEN_NAME));
							// System.out.println(MyAdvapi.SE_INCREASE_QUOTA_NAME+" "+this.doesUserHavePrivilege(MyAdvapi.SE_INCREASE_QUOTA_NAME));
							// System.out.println(MyAdvapi.SE_DEBUG_NAME+" "+this.doesUserHavePrivilege(MyAdvapi.SE_DEBUG_NAME));
							// System.out.println(MyAdvapi.SE_TCB_NAME+" "+this.doesUserHavePrivilege(MyAdvapi.SE_TCB_NAME));
							// */
							/**/if (result)
							{
								// result =
								// setPrivilege(hTokenSelf.getValue(),
								// MyAdvapi.SE_ASSIGNPRIMARYTOKEN_NAME, true)
								// && setPrivilege(hTokenSelf.getValue(),
								// MyAdvapi.SE_INCREASE_QUOTA_NAME, true)
								// && setPrivilege(hTokenSelf.getValue(),
								// MyAdvapi.SE_DEBUG_NAME, true)
								// && setPrivilege(hTokenSelf.getValue(),
								// MyAdvapi.SE_TCB_NAME, true)
								;
							}
							// System.out.println(MyAdvapi.SE_ASSIGNPRIMARYTOKEN_NAME+" "+this.doesUserHavePrivilege(MyAdvapi.SE_ASSIGNPRIMARYTOKEN_NAME));
							// System.out.println(MyAdvapi.SE_INCREASE_QUOTA_NAME+" "+this.doesUserHavePrivilege(MyAdvapi.SE_INCREASE_QUOTA_NAME));
							// System.out.println(MyAdvapi.SE_DEBUG_NAME+" "+this.doesUserHavePrivilege(MyAdvapi.SE_DEBUG_NAME));
							// System.out.println(MyAdvapi.SE_TCB_NAME+" "+this.doesUserHavePrivilege(MyAdvapi.SE_TCB_NAME));
							// */
							// MyKernel32.INSTANCE.CloseHandle(hTokenSelf.getValue());
							if (!doesUserHavePrivilege(MyAdvapi.SE_ASSIGNPRIMARYTOKEN_NAME))
								log("Process does not have the SE_ASSIGNPRIMARYTOKEN_NAME privilege !!");

							if (!doesUserHavePrivilege(MyAdvapi.SE_INCREASE_QUOTA_NAME))
								log("Process does not have the SE_INCREASE_QUOTA_NAME privilege !!");

							result = MyAdvapi.INSTANCE.LogonUserA(stUser, stDomain, stPassword, MyAdvapi.LOGON32_LOGON_INTERACTIVE,
									MyAdvapi.LOGON32_PROVIDER_DEFAULT, phToken);
							if (result)
								// result =
								// MyAdvapi.INSTANCE.CreateProcessWithLogonW(user,
								// domain, password,
								// MyAdvapi.LOGON_NETCREDENTIALS_ONLY, null,
								// cmd, creationFlag, null, wDir, _startupInfo,
								// _processInformation);
								result = MyAdvapi.INSTANCE.CreateProcessAsUserW(phToken.getValue(), null, cmd, null, // sap,
										null, true,// _pipeStreams,
										creationFlag, null, null,// getWorkingDir(),
										_startupInfo, _processInformation);
						}

					}
					/**/

				}
				else
				{
					/*
					 * _startupInfo = new STARTUPINFO(); _startupInfo.clear();
					 * _processInformation = new PROCESS_INFORMATION();
					 * _processInformation.clear();
					 */
					PointerByReference phToken = new PointerByReference();

					String stUser = user.toString();
					String stDomain = domain == null ? null : domain.toString();
					String stPassword = password == null ? "" : password.toString();

					result = MyAdvapi.INSTANCE.LogonUserW(user, domain, password, MyAdvapi.LOGON32_LOGON_INTERACTIVE,
							MyAdvapi.LOGON32_PROVIDER_DEFAULT, phToken);
					log("logonUserA " + result);
					if (result)
					{
						// result =
						// MyAdvapi.INSTANCE.ImpersonateLoggedOnUser(phToken.getValue());
						if (result)
							// result =
							// MyAdvapi.INSTANCE.CreateProcessWithLogonW(user,
							// domain, password,
							// MyAdvapi.LOGON_NETCREDENTIALS_ONLY, null, cmd,
							// creationFlag, null, wDir, _startupInfo,
							// _processInformation);
							result = MyAdvapi.INSTANCE.CreateProcessAsUserW(phToken.getValue(), null, cmd, null, null, _pipeStreams, creationFlag,
									null, new WString(getWorkingDir()), _startupInfo, _processInformation);

					}

				}
			}

			if (!result)
			{
				int err = MyKernel32.INSTANCE.GetLastError();
				log("could not start process " + Integer.toHexString(err));
				log(Kernel32Util.formatMessageFromLastErrorCode(err));
				return result;
			}
			_started = true;

			// Thread.sleep(1000);

			int res = MyUser32.INSTANCE.WaitForInputIdle(_processInformation.hProcess, 2000);
			if (res > 0)
			{
				log("Warning: WaitForInputIdle returned " + res);
				// return false;
			}

			int affinity = getProcessAffinity();
			if (affinity > 0)
			{
				if (!MyKernel32.INSTANCE.SetProcessAffinityMask(_processInformation.hProcess, affinity))
					log("could not set process affinity");
			}

			if (_pipeStreams)
			{

				// Thread.sleep(15000);
				/*
				 * Memory buf = new Memory(1); IntByReference rres = new
				 * IntByReference(); System.out.println("readddd"); boolean x =
				 * MyKernel32.INSTANCE.ReadFile(outRead.getValue(), buf, (int)
				 * buf.getSize(), rres, null); if (!x)
				 * System.out.println("read error"); else
				 * System.out.println(buf.getByte(0));
				 */

				writefd(in_fd, inWrite.getValue());
				writefd(out_fd, outRead.getValue());
				writefd(err_fd, errRead.getValue());

				_outputStream = new BufferedOutputStream(new FileOutputStream(in_fd));
				_inputStream = new BufferedInputStream(new FileInputStream(out_fd));
				_errorStream = new BufferedInputStream(new FileInputStream(err_fd));

				MyKernel32.INSTANCE.CloseHandle(inRead.getValue());
				MyKernel32.INSTANCE.CloseHandle(outWrite.getValue());
				MyKernel32.INSTANCE.CloseHandle(errWrite.getValue());

			}
			else if (_teeName != null && _tmpPath != null)
			{
				File f = new File(_tmpPath);
				if (!f.exists())
					f.mkdir();
				_outputStream = new CyclicBufferFilePrintStream(new File(_tmpPath, "in_" + _teeName));
				_inputStream = new CyclicBufferFileInputStream(new File(_tmpPath, "out_" + _teeName));
				_errorStream = new CyclicBufferFileInputStream(new File(_tmpPath, "err_" + _teeName));
			}

			_pid = _processInformation.dwProcessId;
		}
		catch (Exception ex)
		{
			log("exception in process start: " + ex);
			ex.printStackTrace();
		}

		return result;
	}

	/**
	 * Gets the process affinity.
	 *
	 * @return the process affinity
	 */
	private int getProcessAffinity()
	{
		if (_cpuAffinity <= 0)
			return 0;
		IntByReference lpProcessAffinityMask = new IntByReference();
		IntByReference lpSystemAffinityMask = new IntByReference();
		if (MyKernel32.INSTANCE.GetProcessAffinityMask(_processInformation.hProcess, lpProcessAffinityMask, lpSystemAffinityMask))
			return lpSystemAffinityMask.getValue() & _cpuAffinity;
		else
		{
			log("could not get process affinity mask -> not setting");
			return 0;
		}
	}

	/**
	 * Gets the priority flag.
	 *
	 * @return the priority flag
	 */
	private int getPriorityFlag()
	{
		switch (_priority)
		{
		case PRIORITY_NORMAL:
			return MyKernel32.NORMAL_PRIORITY_CLASS;
		case PRIORITY_ABOVE_NORMAL:
			return MyKernel32.ABOVE_NORMAL_PRIORITY_CLASS;
		case PRIORITY_HIGH:
			return MyKernel32.HIGH_PRIORITY_CLASS;
		case PRIORITY_BELOW_NORMAL:
			return MyKernel32.BELOW_NORMAL_PRIORITY_CLASS;
		case PRIORITY_LOW:
			return MyKernel32.IDLE_PRIORITY_CLASS;
		default:
			return 0;
		}
	}

	// fd.handle = pointer.peer, using reflection, since both are private
	/*
	 * (non-Javadoc)
	 *
	 * @see org.rzo.yajsw.os.Process#waitFor()
	 */
	public void waitFor()
	{
		if (!isRunning())
			return;
		waitFor(MyKernel32.INFINITE);
	}

	/**
	 * Gets the exit code internal.
	 *
	 * @return the exit code internal
	 */
	private int getExitCodeInternal()
	{
		IntByReference code = new IntByReference();
		if (_processInformation == null)
			return -1;
		boolean result = MyKernel32.INSTANCE.GetExitCodeProcess(_processInformation.hProcess, code);
		try
		{
			// if server overloaded windows may need some time to set the exit
			// code.
			Thread.sleep(100);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("get exit code internal " + result + " " +
		// code.getValue());
		if (result)
		{
			if (_debug)
				log("GetExitCodeProcess returned " + code.getValue());
			return code.getValue();
		}
		else
		{
			log("Error in GetExitCodeProcess OS Error #" + MyKernel32.INSTANCE.GetLastError());
			return -3;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.rzo.yajsw.os.Process#waitFor(int)
	 */
	public void waitFor(long timeout)
	{
		if (!isRunning())
			return;
		if (timeout > Integer.MAX_VALUE)
			timeout = Integer.MAX_VALUE;
		if (_processInformation == null)
			return;
		int result = MyKernel32.INSTANCE.WaitForSingleObject(_processInformation.hProcess, (int) timeout);
		if (result == MyKernel32.WAIT_FAILED)
		{
			int errNr = MyKernel32.INSTANCE.GetLastError();
			log("Error in Process.waitFor OS Error #" + errNr + " " + Kernel32Util.formatMessageFromLastErrorCode(errNr));
		}
		else if (result != MyKernel32.WAIT_OBJECT_0)
		{
			log("Error in Process.waitFor OS result #" + result + " " + Kernel32Util.formatMessageFromLastErrorCode(result));
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.rzo.yajsw.os.Process#stop(int, int)
	 */
	public boolean stop(int timeout, int code)
	{
		if (_pid <= 0)
		{
			log("cannot kill process with negative pid " + _pid);
			return false;
		}
		// first try polite kill
		// e.g. post WM_CLOSE to all windows whose PID
		// matches our process.
		MyUser32.WNDENUMPROC closeWindow = new MyUser32.WNDENUMPROC()
		{
			// lParam is the pid of our process
			public boolean callback(Pointer wnd, int lParam)
			{
				// get the pid of the window
				IntByReference dwID = new IntByReference();
				MyUser32.INSTANCE.GetWindowThreadProcessId(wnd, dwID);
				// if this windows belongs to our process
				if (dwID.getValue() == lParam)
				{
					// System.out.println("post message a: " + wnd);
					MyUser32.INSTANCE.PostMessageA(wnd, MyUser32.WM_CLOSE, null, null);
					// MyUser32.INSTANCE.PostMessageA(wnd, MyUser32.WM_QUIT,
					// null, null) ;
					// MyUser32.INSTANCE.PostMessageA(wnd, MyUser32.WM_DESTROY,
					// null, null) ;
				}
				// continue with next window
				return true;
			}
		};
		// execute closeWindow on all windows
		MyUser32.INSTANCE.EnumWindows(closeWindow, _pid);
		try
		{
			Thread.sleep(100);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}

		// Wait for process to terminate

		if (timeout > 0)
			waitFor(timeout);

		// give system time to put exit code
		try
		{
			Thread.sleep(100);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// If still running -> hard kill
		if (isRunning())
		{
            System.out.println( "process is not polite -> hard kill" );
			return kill(code);
		}
		else
		{
			// _processInformation = null;
			_pid = -1;
			setExitCode(code);
			return true;
		}
	}

	// public boolean cleanKill(int code, int timeout)
	// {
	// // first try polite kill
	// // e.g. post WM_CLOSE to all windows whose PID
	// // matches our process.
	// MyUser32.WNDENUMPROC closeWindow = new MyUser32.WNDENUMPROC()
	// {
	// // lParam is the pid of our process
	// public boolean callback(Pointer wnd, int lParam)
	// {
	// // get the pid of the window
	// IntByReference dwID = new IntByReference();
	// MyUser32.INSTANCE.GetWindowThreadProcessId(wnd, dwID) ;
	// // if this windows belongs to our process
	// if(dwID.getValue() == lParam)
	// {
	// MyUser32.INSTANCE.PostMessageA(wnd, MyUser32.WM_CLOSE, null, null) ;
	// }
	// // continue with next window
	// return true;
	// }
	// };
	// // execute closeWindow on all windows
	// MyUser32.INSTANCE.EnumWindows(closeWindow ,
	// _pid) ;
	//
	// // Wait for process to terminate
	// waitFor(timeout);
	// // If still running -> hard kill
	// if (isRunning())
	// return kill(code);
	// return false;
	//
	// }

	/*
	 * (non-Javadoc)
	 *
	 * @see org.rzo.yajsw.os.Process#kill(int)
	 */
	public boolean kill(int code)
	{
		boolean result = false;
		try
		{
			if (_pid <= 0)
			{
				log("cannot kill process with pid " + _pid);
				return false;
			}

			if (!isRunning())
			{
				// _processInformation = null;
				_pid = -1;
				return false;
			}
			int i = 0;
			if (_processInformation != null && _processInformation.hProcess != null)
				while (!result && i < 10)
				{
					if (_processInformation != null && _processInformation.hProcess != null)
					{
						result = MyKernel32.INSTANCE.TerminateProcess(_processInformation.hProcess, code);
						if (!result)
						{
							log("kill of process with PID " + _pid + " failed: OS Error #" + MyKernel32.INSTANCE.GetLastError());
							i++;
							try
							{
								Thread.sleep(500);
							}
							catch (InterruptedException e)
							{
								e.printStackTrace();
								Thread.currentThread().interrupt();
							}

						}
					}
					else
					{
						Thread.sleep(1000);
						result = !isRunning();
					}

				}
			Thread.sleep(100);
			if (!isRunning())
				_pid = -1;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			result = true;
		}

		if (!result)
			log("kill failed: " + _pid + " process still running");

		return result;
	}

	/**
	 * Kill.
	 *
	 * @param pid
	 *            the pid
	 * @param code
	 *            the code
	 *
	 * @return true, if successful
	 */
	public static boolean kill(int pid, int code)
	{
		if (pid <= 0)
			return false;
		HANDLE hProcess = MyKernel32.INSTANCE.OpenProcess(MyKernel32.PROCESS_TERMINATE, false, pid);
		boolean result = MyKernel32.INSTANCE.TerminateProcess(hProcess, code);
		Thread.yield();
		if (!result)
			System.out.println("process kill failed: " + pid + " code=" + code);
		MyKernel32.INSTANCE.CloseHandle(hProcess);
		return result;
	}

	/** The levels. */
	int					levels;

	/** The _pf counter. */
	private PdhCounter	_pfCounter;

	/** The _v mem counter. */
	private PdhCounter	_vMemCounter;

	/** The _cpu counter. */
	private PdhCounter	_cpuCounter;

	/** The _p mem counter. */
	private PdhCounter	_pMemCounter;

	private PdhCounter	_threadCounter;
	private PdhCounter	_handleCounter;

	private boolean		_started	= false;

	/*
	 * (non-Javadoc)
	 *
	 * @see org.rzo.yajsw.os.AbstractProcess#getExitCode()
	 */
	@Override
	public int getExitCode()
	{
		int result = 0;
		if (_exitCode < 0 && _processInformation != null)
		{
			result = getExitCodeInternal();
			if (result != MyKernel32.STILL_ACTIVE)
				setExitCode(result);
			else
				setExitCode(-2);
		}
		else
		{
			// log("getExitCode "+_exitCode + " "+_processInformation);
		}
		if (_debug)
			log("getExitCode " + _exitCode + " processINFO==null=" + (_processInformation == null));
		// System.out.println("get exit code "+_exitCode);
		return _exitCode;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.rzo.yajsw.os.Process#isRunning()
	 */
	public boolean isRunning()
	{
		if (_pid <= 0)
		{
			if (_debug)
				log("is running: false pid=(" + _pid + "<=0)");
			// log("is running: false "+_pid);
			return false;
		}
		if (_processInformation == null)
		{
			log("is running: _processInformation == null pid=" + _pid);
			return false;
		}
		// return _processInformation != null && getExitCode() < 0 && _pid > 0;
		boolean result = getExitCode() == -2 && _pid >= 0;
		// log("is running: "+result +" "+_pid + " "+ _exitCode);
		if (_debug)
			log("is running: " + result + " " + _pid + " " + _exitCode);
		return result;
		/*
		 * Pointer process =
		 * MyKernel32.INSTANCE.OpenProcess(MyKernel32.PROCESS_QUERY_INFORMATION,
		 * false, _pid); if (process == Pointer.NULL) {
		 * log("is running: false "+_pid); return false; }
		 * MyKernel32.INSTANCE.CloseHandle(process);
		 * log("is running: true "+_pid); return true;
		 */

	}

	// if you use counters: you will have to destroy before finalze is called.
	// Otherwise the JVM may crash
	/*
	 * (non-Javadoc)
	 *
	 * @see org.rzo.yajsw.os.Process#destroy()
	 */
	volatile boolean	destroyed	= false;

	public void destroy()
	{
		if (destroyed)
			return;
		destroyed = true;
		if (_processInformation != null)
		{
			if (_teeName != null && _inputStream != null)
			{
				try
				{
					_inputStream.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				try
				{
					_outputStream.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				try
				{
					_errorStream.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				_inputStream = null;
				_outputStream = null;
				_errorStream = null;
				new File(_tmpPath, "in_" + _teeName).delete();
				new File(_tmpPath, "out_" + _teeName).delete();
				new File(_tmpPath, "err_" + _teeName).delete();

			}
			// else
			// System.out.println("no streams to destroy");

			if (outRead != null && outRead.getValue() != Pointer.NULL)
			{
				MyKernel32.INSTANCE.SetHandleInformation(outRead.getValue(), 2, 0);
				MyKernel32.INSTANCE.CloseHandle(outRead.getValue());
				outRead = null;
			}

			if (errRead != null && errRead.getValue() != Pointer.NULL)
			{
				MyKernel32.INSTANCE.SetHandleInformation(errRead.getValue(), 2, 0);
				MyKernel32.INSTANCE.CloseHandle(errRead.getValue());
				errRead = null;
			}

			if (inWrite != null && inWrite.getValue() != Pointer.NULL)
			{
				MyKernel32.INSTANCE.SetHandleInformation(inWrite.getValue(), 2, 0);
				MyKernel32.INSTANCE.CloseHandle(inWrite.getValue());
				inWrite = null;
			}

			if (_processInformation.hThread != null)
				if (!_processInformation.hThread.equals(Pointer.NULL))
					MyKernel32.INSTANCE.CloseHandle(_processInformation.hThread);
			if (_processInformation.hProcess != null)
				if (!_processInformation.hProcess.equals(Pointer.NULL))
					MyKernel32.INSTANCE.CloseHandle(_processInformation.hProcess);
			if (_cpuCounter != null)
			{
				_cpuCounter.close();
				_cpuCounter = null;
			}
			if (_vMemCounter != null)
			{
				_vMemCounter.close();
				_vMemCounter = null;
			}
			if (_pMemCounter != null)
			{
				_pMemCounter.close();
				_pMemCounter = null;
			}

			if (_pfCounter != null)
			{
				_pfCounter.close();
				_pfCounter = null;
			}
			if (_threadCounter != null)
			{
				_threadCounter.close();
				_threadCounter = null;
			}
			if (_handleCounter != null)
			{
				_handleCounter.close();
				_handleCounter = null;
			}
		}
		log("process handles destroyed " + _pid);
		_processInformation = null;

		_startupInfo = null;
		/*
		 * if (_pipeStreams) { if (inRead != null) if (inRead.getValue() !=
		 * null) MyKernel32.INSTANCE.CloseHandle(inRead.getValue()); if
		 * (outWrite != null) if (outWrite.getValue() != null)
		 * MyKernel32.INSTANCE.CloseHandle(outWrite.getValue()); if (errWrite !=
		 * null) if (errWrite.getValue() != null)
		 * MyKernel32.INSTANCE.CloseHandle(errWrite.getValue()); }
		 *
		 * if (_outputStream != null) { try { _outputStream.close(); } catch
		 * (IOException e) { } _outputStream = null; }
		 *
		 * if (_errorStream != null) { try { _errorStream.close(); } catch
		 * (IOException e) { } _errorStream = null; }
		 *
		 * if (_inputStream != null) { try { _inputStream.close(); } catch
		 * (IOException e) { } _inputStream = null; }
		 */

	}

	/**
	 * Read virtual memory to structure.
	 *
	 * @param baseAddress
	 *            the base address
	 * @param goal
	 *            the goal
	 *
	 * @return true, if successful
	 */
	boolean readVirtualMemoryToStructure(Pointer baseAddress, Structure goal)
	{
		int size = goal.size();
		// System.out.println("readVirtualMemoryToStructure "+size);
		int ret = Ntdll.INSTANCE.ZwReadVirtualMemory(_processInformation.hProcess.getPointer(), baseAddress, goal.getPointer(), size, null);
		if (ret != 0)
			log("pid " + _pid + " ZwReadVirtualMemory returns " + Integer.toHexString(ret));

		goal.read();
		return ret == 0;

	}

	/**
	 * Read virtual memory to memory.
	 *
	 * @param baseAddress
	 *            the base address
	 * @param goal
	 *            the goal
	 *
	 * @return true, if successful
	 */
	boolean readVirtualMemoryToMemory(Pointer baseAddress, Memory goal)
	{
		int size = (int) goal.getSize();
		// System.out.println("readVirtualMemoryToMemory "+size);
		int ret = Ntdll.INSTANCE.ZwReadVirtualMemory(_processInformation.hProcess.getPointer(), baseAddress, goal, size, null);
		if (ret != 0)
			log("pid " + _pid + " ZwReadVirtualMemory returns " + Integer.toHexString(ret));

		return ret == 0;

	}

	/**
	 * Gets the command line internal. this works only for 32 bit processes
	 *
	 * @return the command line internal
	 */
	String getCommandLineInternal()
	{
		// System.out.println("get command internal "+getPid());
		String result = "?";
        Ntdll.PROCESS_BASIC_INFORMATION pbi = null;

		pbi = new Ntdll.PROCESS_BASIC_INFORMATION();
		IntByReference returnLength = new IntByReference();
		HANDLE hProcess = _processInformation.hProcess;
		int size = pbi.size();
		int ret = Ntdll.INSTANCE.ZwQueryInformationProcess(hProcess, (byte) 0, pbi.getPointer(), size, returnLength);
		if (ret == 0)
		{
			pbi.read();
			if (pbi.PebBaseAddress != null)
			{
                Ntdll.PEB peb = new Ntdll.PEB();
				// System.out.println(""+1);
				if (readVirtualMemoryToStructure(pbi.PebBaseAddress, peb))
					if (peb.ProcessParameters != null)
					{
                        Ntdll.RTL_USER_PROCESS_PARAMETERS userParams = new Ntdll.RTL_USER_PROCESS_PARAMETERS();
						// System.out.println(""+2);
						if (readVirtualMemoryToStructure(peb.ProcessParameters, userParams))
						{
							// System.out.println("MaximumLength "+userParams.CommandLine.MaximumLength);
							if (userParams.CommandLine.MaximumLength > 0)
							{
								Memory stringBuffer = new Memory(userParams.CommandLine.MaximumLength);
								// System.out.println(""+3);
								if (readVirtualMemoryToMemory(userParams.CommandLine.Buffer, stringBuffer))
									result = stringBuffer.getString(0, true);
							}
							// System.out.println("MaximumLength "+userParams.CommandLine.MaximumLength);
							if (userParams.CurrentDirectoryPath.MaximumLength > 0)
							{
								Memory stringBuffer = new Memory(userParams.CurrentDirectoryPath.MaximumLength);
								if (readVirtualMemoryToMemory(userParams.CurrentDirectoryPath.Buffer, stringBuffer))
									_workingDir = stringBuffer.getString(0, true);
							}
							if (userParams.WindowTitle.MaximumLength > 0)
							{
								Memory stringBuffer = new Memory(userParams.WindowTitle.MaximumLength);
								if (readVirtualMemoryToMemory(userParams.WindowTitle.Buffer, stringBuffer))
									_title = stringBuffer.getString(0, true);
							}
							if (userParams.Environment != null)
							{
								// get size of environment strings
                        MyKernel32.MEMORY_BASIC_INFORMATION memInfo = new MyKernel32.MEMORY_BASIC_INFORMATION();
								int ll = MyKernel32.INSTANCE.VirtualQueryEx(hProcess.getPointer(), userParams.Environment, memInfo.getPointer(),
										memInfo.size());
								memInfo.read();
								if (ll == 0)
								{
									_logger.warning("error getting environment in VirtualQueryEx " + Native.getLastError());
								}
								else if (MyKernel32.PAGE_NOACCESS == memInfo.Protect || MyKernel32.PAGE_EXECUTE == memInfo.Protect)
								{
									_logger.warning("error getting environment in VirtualQueryEx no access right");
								}
								else
								{

									Memory mem = new Memory(memInfo.RegionSize);
									readVirtualMemoryToMemory(userParams.Environment, mem);

									List<String> envStrings = new ArrayList<String>();

									String env = null;
									int l = 0;
									while (!"".equals(env))
									{
										env = mem.getString(l, true);
										if (env != null && env.length() != 0)
										{
											envStrings.add(env);
											l += env.length() * 2 + 2;
										}
										if (env == null)
											break;
									}

									parseEnvString(envStrings);
								}
							}
						}

					}
			}
		}
		// else
		// System.out.println("3 pid " + _pid +
		// " ZwQueryInformationProcess returns " + Integer.toHexString(ret));
		if (result != null)
			result = result.trim();
		return result;

	}

	private void parseEnvString(List<String> envStrings)
	{
		if (envStrings == null || envStrings.size() == 0)
			return;
		for (String str : envStrings)
		{
			String[] var = str.split("=");
			if (var.length == 2)
				_environment.add(new String[]
				{ var[0], var[1] });
		}

	}

    // this should run on all platforms
    // TODO optimize by calling windows methods for WMI
    // note: Runtime.exec("cmd /C wmic") hangs
    // note: we cannot use p.getInputStream() since the result stream contains
    // unexpeced characters
    // note: when we write the result to file we have to convert the string.
    public String getCommandLineInternalWMI()
    {
        String result = "?";
        WindowsXPProcess p = null;
        // if the server is overloaded we may not get an answer -> try 3 times
        for ( int k = 0; k < 3 && "?".equals( result ); k++ )
            try
            {

                p = new WindowsXPProcess();
                new File( "wmic.tmp" ).delete();
                p.setCommand( "cmd /C wmic process where processid=" + getPid()
                              + " get commandline > wmic.tmp" );
                p.setVisible( false );
                p.start();
                p.waitFor( 30000 );
                BufferedReader br = new BufferedReader( new FileReader(
                        "wmic.tmp" ) );
                br.readLine();
                br.readLine();
                String l = br.readLine();
                if ( l.codePointAt( 0 ) == 0 )
                {
                    StringBuffer s = new StringBuffer();
                    for ( int i = 0; i < l.length(); i++ )
                        if ( l.codePointAt( i ) != 0 )
                            s.append( l.charAt( i ) );
                    l = s.toString();
                }
                br.close();
                result = l;
                p.destroy();
            }
            catch ( Exception e )
            {
                if ( _debug ) log( "Error in getCommandLineInternalWMI" );
                e.printStackTrace();
                try
                {
                    Thread.sleep( 10000 );
                }
                catch ( InterruptedException e1 )
                {
                    e1.printStackTrace();
                    return result;
                }
                if ( p != null ) p.destroy();
            }
        return result;

    }

    // test
	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args)
	{
		/*
		 * WindowsXPProcess[] p = new WindowsXPProcess[1]; for (int i = 0; i <
		 * p.length; i++) { p[i] = new WindowsXPProcess(); //
		 * p[i].setPipeStreams(true, false);
		 * p[i].setCommand("ping 127.0.0.1 -t");// "c:/driwin/dripc.exe");// //
		 * "java -cp yajsw.jar // org.rzo.yajsw.HelloWorld > // t.log"); //
		 * p[i].setWorkingDir("c:/driwin"); p[i].setVisible(false);
		 * p[i].setPipeStreams(true, false); } boolean done = false; while
		 * (!done) { done = true; System.out.println("START");
		 *
		 * for (int i = 0; i < p.length; i++) {
		 *
		 * p[i].start(); //
		 * System.out.println(p[i].getCommandLineInternalWMI());
		 *
		 * / String line = null; int k = 0; try { InputStreamReader isr = new
		 * InputStreamReader(p[i].getInputStream()); BufferedReader br = new
		 * BufferedReader(isr);
		 *
		 * line = br.readLine(); System.out.println(line); while (k < 30 && line
		 * != null) { System.out.println(line); line = br.readLine(); k++; }
		 *
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } /
		 *
		 * System.out.println("sleep"); p[i].waitFor(5000); }
		 *
		 * System.out.println("KILL"); for (int i = 0; i < p.length; i++) { //
		 * p[i].killTree(999); ((WindowsXPProcess) p[i]).stop(5000, 999);
		 * System.out.println(p[i].getExitCode()); // p[i].finalize(); } try {
		 * Thread.sleep(1000); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } }
		 *
		 * // p.setCommand("java -classpath z:\dev\yajsw\wrapper.jar org.rzo." )
		 */
		/*
		 * WindowsXPProcess p = new WindowsXPProcess(); p.setCommand("notepad");
		 * p.setUser("test\\yajsw"); p.setPassword("yajsw"); p.start();
		 */
		// getProcess(3332);
		Process p = new WindowsXPProcess();
		// p.setCommand("ping 127.0.0.1");
		p.setCommand("set.bat");
        // List<String[]> env =
        // OperatingSystem.instance().processManagerInstance().getProcess(
        // OperatingSystem.instance().processManagerInstance().currentProcessId()).getEnvironment();
        // p.setEnvironment(env);
        // System.out.println(p.getEnvironmentAsMap().get("Path"));
        // System.out.println(env.get(0)[0]);
		p.setPipeStreams(true, false);
		p.start();
		String line = null;
		int k = 0;
		try
		{
			InputStreamReader isr = new InputStreamReader(p.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			line = br.readLine();
			System.out.println(line);
			while (k < 30 && line != null)
			{
				System.out.println(line);
				line = br.readLine();
				k++;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}

	/**
	 * Reconnect streams.
	 *
	 * @return true, if successful
	 */
	public boolean reconnectStreams()
	{
		if (_teeName != null)
			try
			{
				_inputStream = new CyclicBufferFileInputStream(new File(_tmpPath, "out_" + _teeName));
				_errorStream = new CyclicBufferFileInputStream(new File(_tmpPath, "err_" + _teeName));
				_outputStream = new CyclicBufferFilePrintStream(new File(_tmpPath, "in_" + _teeName));
				return true;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}

		return false;

	}

	/**
	 * Writefd.
	 *
	 * @param fd
	 *            the fd
	 * @param pointer
	 *            the pointer
	 */
	private void writefd(FileDescriptor fd, Pointer pointer)
	{
		try
		{
			// Field[] fields = FileDescriptor.class.getDeclaredFields();
			// System.out.println("fields");
			// for (Field field : fields){
			// System.out.println(field.getName());
			// }
			// System.out.println("writefd");
			Field handleField = FileDescriptor.class.getDeclaredField("handle");
			handleField.setAccessible(true);
			Field peerField = Pointer.class.getDeclaredField("peer");
			peerField.setAccessible(true);
			long value = peerField.getLong(pointer);
			// System.out.println(value);
			// System.out.flush();
			handleField.setLong(fd, value);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public String currentUser()
	{
		String result = System.getenv("USERDOMAIN") + "\\" + System.getenv("USERNAME");
		result = result.toUpperCase();
		return result;
	}

	public String currentUserName()
	{
		String result = System.getProperty("user.name");
		if (result == null)
			return "";
		result = result.toUpperCase();
		return result;
	}

	public String currentUserDomain()
	{
		String result = System.getenv("USERDOMAIN");
		if (result == null)
			return "";
		return result.toUpperCase();
	}

	public String standardizeUser(String user)
	{
		if (user == null)
			return null;
		if (user.indexOf("\\") == -1)
			return currentUserDomain() + "\\" + user.toUpperCase();
		return user.toUpperCase();
	}

	boolean doesUserHavePrivilege(String lpPrivilegeName)

	{
		PointerByReference hToken = new PointerByReference();
		IntByReference dwSize = new IntByReference();
		Memory lpPrivileges;
		MyAdvapi.LUID PrivilegeLuid = new MyAdvapi.LUID();
		int i;
		boolean bResult = false;

		if (!MyAdvapi.INSTANCE.OpenProcessToken(MyKernel32.INSTANCE.GetCurrentProcess(), MyAdvapi.INSTANCE.TOKEN_QUERY, hToken))
			return false;

		MyAdvapi.INSTANCE.GetTokenInformation(hToken.getValue(), MyAdvapi.TokenPrivileges, null, 0, dwSize);

		lpPrivileges = new Memory(dwSize.getValue());

		if (!MyAdvapi.INSTANCE.GetTokenInformation(hToken.getValue(), MyAdvapi.TokenPrivileges, lpPrivileges, dwSize.getValue(), dwSize))
		{
			return false;
		}

		MyKernel32.INSTANCE.CloseHandle(hToken.getValue());

		if (!MyAdvapi.INSTANCE.LookupPrivilegeValueA(null, lpPrivilegeName, PrivilegeLuid))
		{
			return false;
		}

		MyAdvapi.TOKEN_PRIVILEGES privileges = new MyAdvapi.TOKEN_PRIVILEGES(lpPrivileges);
		for (i = 0; i < privileges.PrivilegeCount; i++)
		{
			if (privileges.Privileges[i].Luid.HighPart == PrivilegeLuid.HighPart && privileges.Privileges[i].Luid.LowPart == PrivilegeLuid.LowPart)
			{
				return true;
			}
		}
		return false;
	}

	public boolean isTerminated()
	{
		return (_started && !isRunning());
	}

	public static boolean setWorkingDirectory(String name)
	{
		File f = new File(name);
		String dir;
		if (!f.exists() || !f.isDirectory())
		{
			System.out.println("setWorkingDirectory failed. file not found " + name);
			return false;
		}
		else
			try
			{
				dir = f.getCanonicalPath();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return false;
			}
		boolean result = MyKernel32.INSTANCE.SetCurrentDirectoryA(dir);
		if (result)
			System.setProperty("user.dir", dir);
		return result;
	}

}
