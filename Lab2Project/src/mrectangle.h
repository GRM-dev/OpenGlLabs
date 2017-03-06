#pragma once
class MRectangle
{
public:
	MRectangle(float x, float y, float size_x, float size_y);
	~MRectangle();
	float x;
	float y;
	float pos_tab[12]; // tablica 12 wspolrzednych (4 wierzcholki)
	float col_tab[12]; // tablica kolorow (4 wierzcholki)
};

