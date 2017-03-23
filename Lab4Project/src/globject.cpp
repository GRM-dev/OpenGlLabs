#include "globject.h"
#include "glm/glm.hpp"

//--------------------------------------------------------------------------------------------
// domyslny konstruktor
glObject::glObject()
{
	Coords = static_cast<float *>(malloc(sizeof(float)));
	Cols = static_cast<float *>(malloc(sizeof(float)));
	Normals = static_cast<float *>(malloc(sizeof(float)));
	nx = 1.0;
	ny = 0.0;
	nz = 0.0;
	col_r = 0.0;
	col_g = 0.0;
	col_b = 0.0;
	lVAO = 0;
}
//--------------------------------------------------------------------------------------------
// domyslny destruktor
glObject::~glObject()
{
	if (Coords != nullptr) free(Coords);
	if (Cols != nullptr) free(Cols);
	if (Normals != nullptr) free(Normals);
}
//--------------------------------------------------------------------------------------------
// ustawia aktualny kolor rysowania prymitywu
void glObject::SetColor(float r, float g, float b)
{
	col_r = r;
	col_g = g;
	col_b = b;
}
//--------------------------------------------------------------------------------------------
// ustawia aktualna normalna
void glObject::SetNormal(float _nx, float _ny, float _nz)
{
	nx = _nx;
	ny = _ny;
	nz = _nz;
}
//--------------------------------------------------------------------------------------------
// usuwa alokowane atrybuty
void glObject::CleanUp()
{
	lVAO = 0;
}
//--------------------------------------------------------------------------------------------
// przygotowuje geometrie elipsoidy
void glObject::MakeEgg(GLfloat a, GLfloat b, GLfloat slices, GLfloat stacks)
{
	GLfloat r1 = 0.0; // promienie wodzace do rozpinania czworokatow
	GLfloat r2;

	GLfloat slice_delta = 2 * a / float(slices);   // przyrost odleglosi okregow na osi OX na iteracje
	GLfloat stack_delta = 360.0 / float(stacks); // przyrost wartosci kata w okregu na iteracje

	GLfloat x = -a; // zacznij rysowac obiekt od x=-a

	BeginObject(GL_TRIANGLES);

	// wierzcholki fasety
	float v1[3];
	float v2[3];
	float v3[3];
	float v4[3];

	// normalna
	float N[3];

	int pom = 0;
	int pom2 = slices / 4;

	while (x <= a) // iteruj wzdluz OX az nie uzyskasz x=a
	{
		if (x + slice_delta < a) // wyznacz r1 z przeksztalconego wzoru na elipse lub przypisz r2=0 dla x=a
		{
			r2 = (sqrt((a*a - (x + slice_delta)*(x + slice_delta)))*b) / a;
		}
		else
		{
			r2 = 0.0;
		}

		GLfloat phi1 = 0.0; // zeruj poczatkowa wartosc kata dla tworzenia okregu
		while (phi1 <= 360.0) // iteruj tworzenie okregu
		{
			pom++;
			if (pom == pom2*2) {
				SetColor(0.0, 0.5, 0.0);
				pom = 0;
			}
			else {
				SetColor(0.0, 0.9, 0.1);
			}
			GLfloat phi2 = phi1 + stack_delta; // oblicz phi2 wzgledem phi1

			v1[0] = x;				 v1[1] = r1*cos(phi1*3.14 / 180); v1[2] = r1*sin(phi1*3.14 / 180);
			v2[0] = x;				 v2[1] = r1*cos(phi2*3.14 / 180); v2[2] = r1*sin(phi2*3.14 / 180);
			v3[0] = x + slice_delta; v3[1] = r2*cos(phi2*3.14 / 180); v3[2] = r2*sin(phi2*3.14 / 180);
			v4[0] = x + slice_delta; v4[1] = r2*cos(phi1*3.14 / 180); v4[2] = r2*sin(phi1*3.14 / 180);

			// pierwszy trojkat
			/* CalcNormal(v1,v2,v3,N);
			Normalize(N);
			SetNormal(N[0],N[1],N[2]);*/
			SetNormal(v1[0], v1[1], v1[2]);
			AddVertex(v1[0], v1[1], v1[2]);
			SetNormal(v2[0], v2[1], v2[2]);
			AddVertex(v2[0], v2[1], v2[2]);
			SetNormal(v3[0], v3[1], v3[2]);
			AddVertex(v3[0], v3[1], v3[2]);

			//drugi trojkat
			/* CalcNormal(v1,v3,v4,N);
			Normalize(N);
			SetNormal(N[0],N[1],N[2]);*/
			SetNormal(v1[0], v1[1], v1[2]);
			AddVertex(v1[0], v1[1], v1[2]);
			SetNormal(v3[0], v3[1], v3[2]);
			AddVertex(v3[0], v3[1], v3[2]);
			SetNormal(v4[0], v4[1], v4[2]);
			AddVertex(v4[0], v4[1], v4[2]);

			phi1 += stack_delta; // zwieksz wartosc kata phi1
		}
		x += slice_delta; // zwieksz odleglosc okregu
		r1 = sqrt(a*a - x*x)*b / a; // wyznacz r1 z przeksztalconego wzoru na elipse
	}
	EndObject();
}
//--------------------------------------------------------------------------------------------
// rozpoczyna tworzenie tablicy VAO dla danego prymitywu
void glObject::BeginObject(GLenum P)
{
	lVAO++;
	// przypisz rodzaj prymitywu do narysowania VAO
	Primitives[lVAO - 1] = P;

	// wyzeruj licznik wspolrzednych
	lCoords[lVAO - 1] = 0;
	Coords = static_cast<float *>(malloc(sizeof(float)));
	Cols = static_cast<float *>(malloc(sizeof(float)));
	Normals = static_cast<float *>(malloc(sizeof(float)));

	if (lVAO > MAX_VAO) ThrowException("Przekroczono maksymalna liczbe VAO w glObject");

	GLuint VAO_id[1];
	// przygotuj tablice VAO
	glGenVertexArrays(1, VAO_id);
	VAO[lVAO - 1] = VAO_id[0];

	glBindVertexArray(VAO[lVAO - 1]);

	GLuint VBO_id[3];
	// przygotuj bufory VBO
	glGenBuffers(3, VBO_id);

	VBO[3 * lVAO - 3] = VBO_id[0];
	VBO[3 * lVAO - 2] = VBO_id[1];
	VBO[3 * lVAO - 1] = VBO_id[2];

}
//--------------------------------------------------------------------------------------------
void glObject::AddVertex(glm::vec3 v)
{
	AddVertex(v[0], v[1], v[2]);
}
// dodaje wierzcholek do listy
void glObject::AddVertex(float x, float y, float z)
{
	lCoords[lVAO - 1] += 3;
	Coords = static_cast<float *>(realloc(Coords, lCoords[lVAO - 1] * sizeof(float)));
	if (Coords == nullptr) ThrowException("glObject:: Blad realokacji pamieci");
	Coords[lCoords[lVAO - 1] - 3] = x;
	Coords[lCoords[lVAO - 1] - 2] = y;
	Coords[lCoords[lVAO - 1] - 1] = z;

	Cols = static_cast<float *>(realloc(Cols, lCoords[lVAO - 1] * sizeof(float)));
	if (Cols == nullptr) ThrowException("glObject:: Blad realokacji pamieci");
	Cols[lCoords[lVAO - 1] - 3] = col_r;
	Cols[lCoords[lVAO - 1] - 2] = col_g;
	Cols[lCoords[lVAO - 1] - 1] = col_b;

	Normals = static_cast<float *>(realloc(Normals, lCoords[lVAO - 1] * sizeof(float)));
	if (Normals == nullptr) ThrowException("glObject:: Blad realokacji pamieci");
	Normals[lCoords[lVAO - 1] - 3] = nx;
	Normals[lCoords[lVAO - 1] - 2] = ny;
	Normals[lCoords[lVAO - 1] - 1] = nz;
}
//--------------------------------------------------------------------------------------------
void glObject::EndObject()
{
	// podlacz pierwszy obiekt z VAOs
	glBindVertexArray(VAO[lVAO - 1]);
	// podlacz pierwszy bufor VBOs
	glBindBuffer(GL_ARRAY_BUFFER, VBO[3 * lVAO - 3]);
	// wypelnij bufor wspolrzednymi wierzcholka

	glBufferData(GL_ARRAY_BUFFER, lCoords[lVAO - 1] * sizeof(float), Coords, GL_STATIC_DRAW);
	// wybierz atrybut indeksie 0 (wskazany w shaderze)
	glEnableVertexAttribArray(0);
	// powiaz dane z bufora ze wskazanym atrybutem
	glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 0, nullptr);

	// podlacz drugi bufor VBOs
	glBindBuffer(GL_ARRAY_BUFFER, VBO[3 * lVAO - 2]);
	// wypelnij bufor kolorami wierzcholka
	glBufferData(GL_ARRAY_BUFFER, lCoords[lVAO - 1] * sizeof(float), Cols, GL_STATIC_DRAW);
	// wybierz atrybut indeksie 1 (wskazany w shaderze)
	glEnableVertexAttribArray(1);
	// powiaz dane z bufora ze wskazanym atrybutem
	glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, 0, nullptr);

	// podlacz trzeci bufor VBOs
	glBindBuffer(GL_ARRAY_BUFFER, VBO[3 * lVAO - 1]);
	// wypelnij bufor kolorami wierzcholka
	glBufferData(GL_ARRAY_BUFFER, lCoords[lVAO - 1] * sizeof(float), Normals, GL_STATIC_DRAW);
	// wybierz atrybut indeksie 2 (wskazany w shaderze)
	glEnableVertexAttribArray(2);
	// powiaz dane z bufora ze wskazanym atrybutem
	glVertexAttribPointer(2, 3, GL_FLOAT, GL_FALSE, 0, nullptr);

	glBindVertexArray(0);
}
//--------------------------------------------------------------------------------------------
void glObject::Draw()
{
	for (int i = 0; i < lVAO; i++)
	{
		glBindVertexArray(VAO[i]);
		glDrawArrays(Primitives[i], 0, lCoords[i] / 3);
		glBindVertexArray(0);
	}
}
//--------------------------------------------------------------------------------------------
int glObject::CalcNormal(glm::vec3 A, glm::vec3 B, glm::vec3 C, glm::vec3* N)
{
	auto x = 0, y = 1, z = 2;
	glm::vec3 U, V;
	// oblicz wspolrzedne wektorow U oraz V
	U = A - B;
	V = B - C;
	// wyznacz wspolrzedne normalnej
	(*N)[x] = U[y] * V[z] - U[z] * V[y];
	(*N)[y] = U[z] * V[x] - U[x] * V[z];
	(*N)[z] = U[x] * V[y] - U[y] * V[x];
	return 1;
}

int glObject::CalcNormal(float A[], float B[], float C[], float *N)
{
	const int x = 0, y = 1, z = 2;
	float U[3], V[3];
	// oblicz wspolrzedne wektorow U oraz V
	U[x] = A[x] - B[x];
	U[y] = A[y] - B[y];
	U[z] = A[z] - B[z];
	V[x] = B[x] - C[x];
	V[y] = B[y] - C[y];
	V[z] = B[z] - C[z];
	// wyznacz wspolrzedne normalnej
	N[x] = U[y] * V[z] - U[z] * V[y];
	N[y] = U[z] * V[x] - U[x] * V[z];
	N[z] = U[x] * V[y] - U[y] * V[x];
	return 1;
}
//--------------------------------------------------------------------------------------------
int glObject::Normalize(glm::vec3 N)
{
	const int x = 0, y = 1, z = 2;
	// oblicz dlugosc wektora
	float L = static_cast<float>(sqrt(N[x] * N[x] + N[y] * N[y] + N[z] * N[z]));
	if (L < 0.01) L = 0.01;
	// wyznacz wspolrzedne normalnej
	N[x] /= L;
	N[y] /= L;
	N[z] /= L;
	return 1;
}

int glObject::Normalize(float *N)
{
	const int x = 0;
	const int y = 1;
	const int z = 2;
	// oblicz dlugosc wektora
	float L = static_cast<float>(sqrt(N[x] * N[x] + N[y] * N[y] + N[z] * N[z]));
	if (L < 0.01) L = 0.01;
	// wyznacz wspolrzedne normalnej
	N[x] /= L;
	N[y] /= L;
	N[z] /= L;
	return 1;
}

void glObject::Move(float wave)
{

}

//--------------------------------------------------------------------------------------------
void glObject::MakeSurf(GLfloat d_x, GLfloat d_z, GLfloat d_fi, GLfloat A, GLfloat k)
{
	GLfloat x = -10, y, z, r;

	GLfloat dx = 20 / d_x;
	GLfloat dz = 20 / d_z;

	glm::vec3 v1, v2, v3, v4, N;

	BeginObject(GL_TRIANGLES);
	SetColor(1.0, 0.0, 0.0);

	while (x <= 10)
	{
		z = -10;
		while (z <= 10)
		{
			auto Y = [](GLfloat x, GLfloat z, GLfloat A, GLfloat k, GLfloat defi)->GLfloat {
				return (A - sqrt(x*x + z*z))*sin((A - sqrt(x*x + z*z))*k + defi); };
			v1[0] = x;      v1[1] = Y(x, z + dz, A, k, d_fi);      v1[2] = z + dz;
			v2[0] = x;      v2[1] = Y(x, z, A, k, d_fi);           v2[2] = z;
			v3[0] = x + dx; v3[1] = Y(x + dx, z, A, k, d_fi);      v3[2] = z;
			v4[0] = x + dx; v4[1] = Y(x + dx, z + dz, A, k, d_fi); v4[2] = z + dz;

			CalcNormal(v1, v2, v3, &N);
			Normalize(N);
			SetNormal(N[0], N[1], N[2]);
			AddVertex(v1);
			AddVertex(v2);
			AddVertex(v3);

			CalcNormal(v2, v3, v4, &N);
			Normalize(N);
			AddVertex(v1);
			AddVertex(v3);
			AddVertex(v4);

			z += dz;
		}
		x += dx;
	}
	EndObject();
}

float glObject::SetInRange(float v)
{
	if (v > 10.0)
		return 10.0;
	if (v < -10.0)
		return -10.0;
	return v;
}

//----------------------------------------------------------------------
void glObject::MakeSurf2(GLfloat dRadius, GLfloat dAngle, GLfloat defi, GLfloat A, GLfloat k)
{
	float pi = 3.14159;
	dRadius = 15 / dRadius;
	dAngle = 2 * pi / dAngle;
	GLfloat c_radius = 0;
	GLfloat angle = 0;

	glm::vec3 v1, v2, v3, v4, N;

	BeginObject(GL_TRIANGLES);
	SetColor(1.0, 0.0, 0.0);

	while (c_radius <= 15)
	{
		angle = 0;
		while (angle <= 2 * pi)
		{
			auto Y = [](GLfloat x, GLfloat z, GLfloat A, GLfloat k, GLfloat defi)->GLfloat {return (A - sqrt(x*x + z*z))*sin((A - sqrt(x*x + z*z))*k + defi); };
			v1[0] = c_radius * cos(angle);
			v1[0] = SetInRange(v1[0]);
			v1[1] = Y(v1[0], v1[2], A, k, defi);
			v1[2] = c_radius * sin(angle);
			v1[2] = SetInRange(v1[2]);

			v2[0] = (c_radius + dRadius) * cos(angle);
			v2[0] = SetInRange(v2[0]);
			v2[1] = Y(v2[0], v2[2], A, k, defi);
			v2[2] = (c_radius + dRadius) * sin(angle);
			v2[2] = SetInRange(v2[2]);

			v3[0] = (c_radius + dRadius) * cos(angle + dAngle);
			v3[0] = SetInRange(v3[0]);
			v3[1] = Y(v3[0], v3[2], A, k, defi);
			v3[2] = (c_radius + dRadius) * sin(angle + dAngle);
			v3[2] = SetInRange(v3[2]);

			v4[0] = c_radius * cos(angle + dAngle);
			v4[0] = SetInRange(v4[0]);
			v4[1] = Y(v4[0], v4[2], A, k, defi);
			v4[2] = c_radius * sin(angle + dAngle);
			v4[2] = SetInRange(v4[2]);

			CalcNormal(v1, v2, v3, &N);
			Normalize(N);
			SetNormal(N[0], N[1], N[2]);
			AddVertex(v1);
			AddVertex(v2);
			AddVertex(v3);

			CalcNormal(v1, v3, v4, &N);
			Normalize(N);
			AddVertex(v1);
			AddVertex(v3);
			AddVertex(v4);

			CalcNormal(v1, v2, v3, &N);
			Normalize(N);
			SetNormal(N[0], N[1], N[2]);
			SetNormal(v1[0], v1[1], v1[2]);
			AddVertex(v1);
			SetNormal(v2[0], v2[1], v2[2]);
			AddVertex(v2);
			SetNormal(v3[0], v3[1], v3[2]);
			AddVertex(v3);

			CalcNormal(v1, v3, v4, &N);
			Normalize(N);
			SetNormal(N[0], N[1], N[2]);
			SetNormal(v1[0], v1[1], v1[2]);
			AddVertex(v1[0], v1[1], v1[2]);
			SetNormal(v3[0], v3[1], v3[2]);
			AddVertex(v3[0], v3[1], v3[2]);
			SetNormal(v4[0], v4[1], v4[2]);
			AddVertex(v4[0], v4[1], v4[2]);

			angle += dAngle;
		}
		c_radius += dRadius;
	}
	EndObject();
}
// the end
