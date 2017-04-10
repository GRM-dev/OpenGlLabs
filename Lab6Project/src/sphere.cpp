#include "sphere.h"

//--------------------------------------------------------------------------------------------
// domyslny konstruktor
glSphere::glSphere(float R, char *TextureFile)
{
	tex = new glTexture(TextureFile);

	// przygotuj geometrie obiektu
	float alpha = 0.0;
	int slices = 50;
	float dalpha = 360.0 / float(slices - 1);
	float bAngle = PI / 180;
	float kat = PI / 180;

	float u = 0.0;
	float du = 1.0 / float(slices - 1);
	float v = 0.0;
	float dv = 1.0 / float(slices - 1);

	float phi = -90.0;
	float dphi = 180.0 / float(slices - 1);

	BeginObject(GL_TRIANGLE_STRIP, tex->Bind());
	SetColor(0.0, 1.0, 1.0);
	while (phi <= 90)
	{
		alpha = 0.0;
		u = 0.0;
		while (alpha <= 360.0)
		{
			float k1 = phi*bAngle;
			float k2 = alpha*bAngle;
			float k3 = (phi + dphi)*bAngle;
			float k4 = (alpha + dalpha)*bAngle;
			AddVertex(R* cos(k1)*cos(k2), R* sin(k1), R * cos(k1) * sin(k2), u, v);
			AddVertex(R* cos(k3)*cos(k2), R* sin(k3), R * cos(k3) * sin(k2), (alpha + dalpha >= 360) ? 1 : u, (phi + dphi >= 360) ? 1 : v + dv);
			AddVertex(R* cos(k1)*cos(k4), R* sin(k1), R * cos(k1) * sin(k4), u + du, v);
			AddVertex(R* cos(k3)*cos(k4), R* sin(k3), R * cos(k3) * sin(k4), (alpha + dalpha >= 360) ? 1 : u + du, (phi + dphi >= 90) ? 1 : v + dv);
			/*AddVertex(R* cos(phi*kat)*cos(alpha*kat), R* sin(phi*kat), R * cos(phi*kat) * sin(alpha*kat), u, v);
			AddVertex(R* cos((phi + dphi)*kat)*cos(alpha*kat), R* sin((phi + dphi)*kat), R * cos((phi + dphi)*kat) * sin(alpha*kat), u, v + dv);
			AddVertex(R* cos(phi*kat)*cos((alpha + dalpha)*kat), R* sin(phi*kat), R * cos(phi*kat) * sin((alpha + dalpha)*kat), u + du, v);
			AddVertex(R* cos((phi + dphi)*kat)*cos((alpha + dalpha)*kat), R* sin((phi + dphi)*kat), R * cos((phi + dphi)*kat) * sin((alpha + dalpha)*kat), u + du, v + dv);
			*/
			//AddVertex(R*cos((alpha*PI) / 180.0)/phi, phi, R*sin((alpha*PI) / 180.0)/phi, u / 360, v / 360);
			//AddVertex(R*cos((alpha*PI) / 180.0)*phi, phi, R*sin((alpha*PI) / 180.0)*phi, u / 360 + 1, v / 360 + 1);
			//AddVertex(R*cos((alpha*PI) / 180.0)*phi, -phi, R*sin((alpha*PI) / 180.0)*phi, u / 360, v / 360);
			//AddVertex(R*cos(alpha*PI / (180.0*phi)), phi, R*sin(alpha*PI / (180.0*phi)), 1, v / 360);
			//AddVertex(R*cos(alpha*PI / (180.0*phi)), -phi, R*sin(alpha*PI / (180.0*phi)), 0, v / 360);
			alpha += dalpha;
			u += du;
		}
		phi += dphi;
		v += dv;
	}
	EndObject();
}
//--------------------------------------------------------------------------------------------
// domyslny destruktor
glSphere::~glSphere()
{
	if (tex) delete tex;
}
//--------------------------------------------------------------------------------------------
// the end