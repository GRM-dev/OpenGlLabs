//--------------------------------------------------------------------------------------------
//
// File: 	main.cpp
// Author:	P. Katarzynski (CCE)
//
// Description: Glowny plik programu
//
//--------------------------------------------------------------------------------------------
//									ZALEZNOSCI
//--------------------------------------------------------------------------------------------
#include "main.h"
#include "scene.h"

//--------------------------------------------------------------------------------------------
// ZMIENNE GLOBALNE
//--------------------------------------------------------------------------------------------
int window;  // uchwyt do okna OGL
Scene *SC; // scena OpenGL

#define  IDT_TIMER  WM_USER + 200

//--------------------------------------------------------------------------------------------
// KOD WINAPI DLA VISUAL STUDIO
//--------------------------------------------------------------------------------------------
HGLRC           hRC = nullptr;
HDC             hDC = nullptr;
HWND            hWnd = nullptr;
HINSTANCE       hInstance;
HWND hListBox = nullptr;

int wHeight = INITIAL_HEIGHT;
int wWidth = INITIAL_WIDTH;

LRESULT	CALLBACK WndProc(HWND, UINT, WPARAM, LPARAM);

//--------------------------------------------------------------------------------------------
// usuwa zasoby zwiazane z oknem OpenGL
GLvoid KillGLWindow(GLvoid)
{
	if (hRC) {
		wglMakeCurrent(nullptr, nullptr);
		wglDeleteContext(hRC);
		hRC = nullptr;
	}

	if (hDC) {
		ReleaseDC(hWnd, hDC);
		hDC = nullptr;
	}

	if (hWnd) {
		DestroyWindow(hWnd);
		hWnd = nullptr;
	}

	UnregisterClass("OpenGL", hInstance);
	hInstance = nullptr;
}
//--------------------------------------------------------------------------------------------
// Alokuje zasoby i tworzy okno aplikacji z OpenGL
BOOL CreateGLWindow(char* title, int width, int height, int bits)
{
	GLuint		PixelFormat;
	WNDCLASS	wc;
	DWORD		dwExStyle;
	DWORD		dwStyle;

	RECT WindowRect;
	WindowRect.left = 0;
	WindowRect.right = static_cast<long>(width);
	WindowRect.top = 0;
	WindowRect.bottom = static_cast<long>(height);

	hInstance = GetModuleHandle(nullptr);
	wc.style = CS_HREDRAW | CS_VREDRAW | CS_OWNDC;
	wc.lpfnWndProc = static_cast<WNDPROC>(WndProc);
	wc.cbClsExtra = 0;
	wc.cbWndExtra = 0;
	wc.hInstance = hInstance;
	wc.hIcon = LoadIcon(nullptr, IDI_WINLOGO);
	wc.hCursor = LoadCursor(nullptr, IDC_ARROW);
	wc.hbrBackground = nullptr;
	wc.lpszMenuName = nullptr;
	wc.lpszClassName = "OpenGL";

	if (!RegisterClass(&wc))
	{
		MessageBox(nullptr,
			"Rejestracja klasy zakonczona niepowodzeniem",
			"ERROR", MB_OK | MB_ICONEXCLAMATION);
		return FALSE;
	}

	dwExStyle = WS_EX_APPWINDOW | WS_EX_WINDOWEDGE;
	dwStyle = WS_OVERLAPPEDWINDOW;

	AdjustWindowRectEx(&WindowRect, dwStyle, FALSE, dwExStyle);

	if (!(hWnd = CreateWindowEx(dwExStyle,
		"OpenGL",
		title,
		WS_CLIPSIBLINGS | WS_CLIPCHILDREN | dwStyle,
		30, 30,
		WindowRect.right - WindowRect.left,
		WindowRect.bottom - WindowRect.top,
		nullptr,
		nullptr,
		hInstance,
		nullptr)))
	{
		KillGLWindow();
		MessageBox(nullptr, "Utworzenie okna zakonczone niepowodzeniem",
			"ERROR", MB_OK | MB_ICONEXCLAMATION);
		return FALSE;
	}

	static	PIXELFORMATDESCRIPTOR pfd =
	{
		sizeof(PIXELFORMATDESCRIPTOR),
		1,
		PFD_DRAW_TO_WINDOW | PFD_SUPPORT_OPENGL | PFD_DOUBLEBUFFER,
		PFD_TYPE_RGBA,
		bits,
		0, 0, 0, 0, 0, 0,
		0,
		0,
		0,
		0, 0, 0, 0,
		16,
		0,
		0,
		PFD_MAIN_PLANE,
		0,
		0, 0, 0
	};

	if (!(hDC = GetDC(hWnd))) {
		KillGLWindow();
		return FALSE;
	}

	if (!(PixelFormat = ChoosePixelFormat(hDC, &pfd))) {
		KillGLWindow();
		return FALSE;
	}

	if (!SetPixelFormat(hDC, PixelFormat, &pfd)) {
		KillGLWindow();
		return FALSE;
	}

	if (!(hRC = wglCreateContext(hDC))) {
		KillGLWindow();
		return FALSE;
	}

	if (!wglMakeCurrent(hDC, hRC)) {
		KillGLWindow();
		return FALSE;
	}


	ShowWindow(hWnd, SW_SHOW);
	SetForegroundWindow(hWnd);
	SetFocus(hWnd);

	hListBox = CreateWindowEx(WS_EX_CLIENTEDGE, "LISTBOX", nullptr, WS_CHILD | WS_VISIBLE | WS_BORDER | WS_VSCROLL,
		0, wHeight - 100, wWidth, 100, hWnd, nullptr, hInstance, nullptr);

	HFONT hLogFont = static_cast<HFONT>(GetStockObject(DEFAULT_GUI_FONT));
	SendMessage(hListBox, WM_SETFONT, reinterpret_cast<WPARAM>(hLogFont), 0);

	SC->Resize(width, height);
	SC->Init();

	return TRUE;
}
//--------------------------------------------------------------------------------------------
// Przetwarza komunikaty systemowe wysylane do okna aplikacji
LRESULT CALLBACK WndProc(HWND hWnd, UINT uMsg, WPARAM wParam, LPARAM	lParam)
{
	switch (uMsg)
	{

	case WM_CREATE: // utworzenie okna

		break;
	case WM_ACTIVATE:         // aktywacja okna
	{
		return 0;
	}
	case WM_PAINT:         // odrysowanie okna
	{
		SC->Draw();
		SwapBuffers(hDC);
		break;
	}
	case WM_TIMER: // zdarzenie timera
	{
		/*	SC->Animate();
			SC->Draw();
			SwapBuffers(hDC);
			*/
		break;
	}
	case WM_SYSCOMMAND:     // zdarzenia systemowe
	{
		switch (wParam)
		{
		case SC_SCREENSAVE:
		case SC_MONITORPOWER:
			return 0;
		}
		break;
	}
	case WM_CLOSE:         // zamkniecie okna
	{
		PostQuitMessage(0);
		return 0;
	}
	case WM_KEYDOWN:      // nacisniecie klawisza
	{
		POINT cPos;
		GetCursorPos(&cPos);
		SC->KeyPressed(wParam, cPos.x, cPos.y);
		SC->Draw();
		SwapBuffers(hDC);
		return 0;
	}
	case WM_KEYUP:        // zwolnienie klawisza
	{
		return 0;
	}
	case WM_SIZE:      // zmiana rozmiaru okna
	{
		// aktualizuj zmienne z rozmiarem okna
		wWidth = LOWORD(lParam);
		wHeight = HIWORD(lParam);

		// przeskaluj scene OGL
		SC->Resize(wWidth, wHeight);

		//przesun kontrolke z logiem
		MoveWindow(hListBox, 0, wHeight - 100, wWidth, 100, true);

		return 0;
	}
	}

	return DefWindowProc(hWnd, uMsg, wParam, lParam);
}

//--------------------------------------------------------------------------------------------
// glowny podprogram
int WINAPI WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPSTR lpCmdLine, int nCmdShow)
{
	MSG	msg;

	try
	{
		SC = new Scene(wWidth, wHeight);

		if (!CreateGLWindow(PROJECT_NAME, wWidth, wHeight, 16)) // utworz okno z widokiem sceny OPENGL
			return 0;

		SetTimer(hWnd, IDT_TIMER, 10, (TIMERPROC) nullptr); // ustaw obiekt timera

		while (GetMessage(&msg, nullptr, 0, 0)) // pobierz komunikat z kolejki systemowej
		{
			TranslateMessage(&msg); // przetwarzaj komunikat w obszarze okna
			DispatchMessage(&msg); // usun komunikat z kolejki systemowej
		}

	}
	catch (char *e)
	{
		MessageBox(nullptr, e, "Wystapi³ wyj¹tek", 0);
	}
	catch (...) // unhandled exceptions
	{
		KillGLWindow(); // usun zasoby okna
		if (SC) delete SC;
	}

	KillTimer(hWnd, IDT_TIMER);
	KillGLWindow(); // usun zasoby okna

	if (SC) delete SC;

	return 0;
}
//--------------------------------------------------------------------------------------------
// koniec pliku