// Based on https://www.shadertoy.com/view/MdBGzK

#define DPII (3.1415926535*2.0)

// 0 -> Blue / Pink
// 1 -> Blue / Green
// 2 -> Black / White
// 3 -> Blue / Teal / Black
// 4 -> Black / White
int GetPlasmaIndex(int t)
{
	return 0;
}

const int initialL1[5] = int[5](1000, 1000, 3500, 1000, 1000);
const int initialL2[5] = int[5](2000, 2000, 1000, 2000, 2000);
const int initialL3[5] = int[5](3000, 4000, 3000, 3000, 3000);
const int initialL4[5] = int[5](4000, 4000, 1000, 4000, 4000);

const int initialK1[5] = int[5](3500, 1500, 3500, 3500, 3500);
const int initialK2[5] = int[5](2300, 2300, 3300, 2300, 2300);
const int initialK3[5] = int[5](3900, 3900, 2900, 3900, 3900);
const int initialK4[5] = int[5](3670, 1670, 2670, 3670, 3670);

int GetC1(int nXOffset0or1, int t)
{
	int nPlasmaIndex = GetPlasmaIndex(t);

	int nResult = 0;
	if (nXOffset0or1 == 0)
	{
		nResult = initialK1[nPlasmaIndex] + (-3 * t);
	}
	else
	{
		nResult = initialL1[nPlasmaIndex] + (-1 * t);
	}
	
	float fResult = float(nResult);
	fResult = mod(fResult, 4096.0);

	if (fResult < 0.0)
	{
		fResult += 4096.0;
	}
	nResult = int(fResult);

	return nResult;
}

int GetC2(int nXOffset0or1, int t)
{
	int nPlasmaIndex = GetPlasmaIndex(t);

	int nResult = 0;
	if (nXOffset0or1 == 0)
	{
		nResult = initialK2[nPlasmaIndex] + (-2 * t);
	}
	else
	{
		nResult = initialL2[nPlasmaIndex] + (-2 * t);
	}
	
	float fResult = float(nResult);
	fResult = mod(fResult, 4096.0);

	if (fResult < 0.0)
	{
		fResult += 4096.0;
	}
	nResult = int(fResult);

	return nResult;
}

int GetC3(int nXOffset0or1, int t)
{
	int nPlasmaIndex = GetPlasmaIndex(t);

	int nResult = 0;
	if (nXOffset0or1 == 0)
	{
		nResult = initialK3[nPlasmaIndex] + (1 * t);
	}
	else
	{
		nResult = initialL3[nPlasmaIndex] + (2 * t);
	}
	
	float fResult = float(nResult);
	fResult = mod(fResult, 4096.0);

	if (fResult < 0.0)
	{
		fResult += 4096.0;
	}
	nResult = int(fResult);

	return nResult;
}

int GetC4(int nXOffset0or1, int t)
{
	int nPlasmaIndex = GetPlasmaIndex(t);

	int nResult = 0;
	if (nXOffset0or1 == 0)
	{
		nResult = initialK4[nPlasmaIndex] + (2 * t);
	}
	else
	{
		nResult = initialL4[nPlasmaIndex] + (3 * t);
	}
	
	float fResult = float(nResult);
	fResult = mod(fResult, 4096.0);

	if (fResult < 0.0)
	{
		fResult += 4096.0;
	}
	nResult = int(fResult);

	return nResult;
}

float Palette0_GetRed(float fIndex)
{
	float a = 0.0;
	if ((fIndex >= 0.0) && (fIndex < 64.0))
	{
		a = fIndex;
	}
	else if ((fIndex >= 64.0) && (fIndex < 128.0))
	{
		a = fIndex - 64.0;
		a = 63.0 - a;
	}
	else if ((fIndex >= 128.0) && (fIndex < 192.0))
	{
		a = 0.0;
	}
	else
	{
		a = fIndex - 192.0;
	}

	float fResult = cos(a*(3.1415926535*2.0)/128.0+3.1415926535)*31.0+32.0;
	fResult /= 63.0;

	return fResult;
}

float Palette0_GetGreen(float fIndex)
{
	float a = 0.0;
	if ((fIndex >= 0.0) && (fIndex < 64.0))
	{
		a = 0.0;
	}
	else if ((fIndex >= 64.0) && (fIndex < 128.0))
	{
		a = 0.0;
	}
	else if ((fIndex >= 128.0) && (fIndex < 192.0))
	{
		a = 0.0;
	}
	else
	{
		a = 0.0;
	}

	float fResult = cos(a*(3.1415926535*2.0)/128.0+3.1415926535)*31.0+32.0;
	fResult /= 63.0;

	return fResult;
}

float Palette0_GetBlue(float fIndex)
{
	float a = 0.0;
	if ((fIndex >= 0.0) && (fIndex < 64.0))
	{
		a = fIndex;
	}
	else if ((fIndex >= 64.0) && (fIndex < 128.0))
	{
		a = fIndex - 64.0;
		a = 63.0 - a;
	}
	else if ((fIndex >= 128.0) && (fIndex < 192.0))
	{
		a = fIndex - 128.0;
	}
	else
	{
		a = fIndex - 192.0;
		a = 63.0 - a;
	}

	float fResult = cos(a*(3.1415926535*2.0)/128.0+3.1415926535)*31.0+32.0;
	fResult /= 63.0;

	return fResult;
}

float Palette1_GetRed(float fIndex)
{
		return 0.0;
		
	float a = 0.0;
	if ((fIndex >= 0.0) && (fIndex < 64.0))
	{
		a = fIndex;
	}
	else if ((fIndex >= 64.0) && (fIndex < 128.0))
	{
		a = fIndex - 64.0;
		a = 63.0 - a;
	}
	else if ((fIndex >= 128.0) && (fIndex < 192.0))
	{
		a = 0.0;
	}
	else
	{
		a = fIndex - 192.0;
	}

	float fResult = cos(a*(3.1415926535*2.0)/128.0+3.1415926535)*31.0+32.0;
	fResult /= 63.0;

	return fResult;
}

float Palette1_GetGreen(float fIndex)
{
	float a = 0.0;
	if ((fIndex >= 0.0) && (fIndex < 64.0))
	{
		a = 0.0;
	}
	else if ((fIndex >= 64.0) && (fIndex < 128.0))
	{
		a = 0.0;
	}
	else if ((fIndex >= 128.0) && (fIndex < 192.0))
	{
		a = fIndex - 128.0;
	}
	else
	{
		a = 63.0;
	}

	float fResult = cos(a*(3.1415926535*2.0)/128.0+3.1415926535)*31.0+32.0;
	fResult /= 63.0;

	return fResult;
}
float Palette1_GetBlue(float fIndex)
{
	float a = 0.0;
	if ((fIndex >= 0.0) && (fIndex < 64.0))
	{
		a = 0.0;
	}
	else if ((fIndex >= 64.0) && (fIndex < 128.0))
	{
		a = fIndex - 64.0;
	}
	else if ((fIndex >= 128.0) && (fIndex < 192.0))
	{
		a = fIndex - 128.0;
		a = 63.0 - a;
	}
	else
	{
		a = fIndex - 192.0;
	}

	float fResult = cos(a*(3.1415926535*2.0)/128.0+3.1415926535)*31.0+32.0;
	fResult /= 63.0;

	return fResult;
}

float Palette2_GetRed(float fIndex)
{
	float a = 0.0;
	if ((fIndex >= 0.0) && (fIndex < 64.0))
	{
		a = 0.0;
	}
	else if ((fIndex >= 64.0) && (fIndex < 128.0))
	{
		a = fIndex - 64.0;
	}
	else if ((fIndex >= 128.0) && (fIndex < 192.0))
	{
		a = fIndex - 128.0;
		a = 63.0 - a;
	}
	else
	{
		a = 0.0;
	}

	float fResult = cos(a*(3.1415926535*2.0)/128.0+3.1415926535)*31.0+32.0;
	fResult /= 2.0;
	fResult /= 63.0;

	return fResult;
}

float Palette2_GetGreen(float fIndex)
{
	float a = 0.0;
	if ((fIndex >= 0.0) && (fIndex < 64.0))
	{
		a = 0.0;
	}
	else if ((fIndex >= 64.0) && (fIndex < 128.0))
	{
		a = fIndex - 64.0;
	}
	else if ((fIndex >= 128.0) && (fIndex < 192.0))
	{
		a = fIndex - 128.0;
		a = 63.0 - a;
	}
	else
	{
		a = 0.0;
	}

	float fResult = cos(a*(3.1415926535*2.0)/128.0+3.1415926535)*31.0+32.0;
	fResult /= 2.0;
	fResult /= 63.0;

	return fResult;
}
float Palette2_GetBlue(float fIndex)
{
	float a = 0.0;
	if ((fIndex >= 0.0) && (fIndex < 64.0))
	{
		a = 0.0;
	}
	else if ((fIndex >= 64.0) && (fIndex < 128.0))
	{
		a = fIndex - 64.0;
	}
	else if ((fIndex >= 128.0) && (fIndex < 192.0))
	{
		a = fIndex - 128.0;
		a = 63.0 - a;
	}
	else
	{
		a = 0.0;
	}

	float fResult = cos(a*(3.1415926535*2.0)/128.0+3.1415926535)*31.0+32.0;
	fResult /= 2.0;
	fResult /= 63.0;

	return fResult;
}

float Palette3_GetRed(float fIndex)
{
		return 0.0;
	float a = 0.0;
	if ((fIndex >= 0.0) && (fIndex < 64.0))
	{
		a = fIndex;
	}
	else if ((fIndex >= 64.0) && (fIndex < 128.0))
	{
		a = 63.0;
	}
	else if ((fIndex >= 128.0) && (fIndex < 192.0))
	{
		a = fIndex - 128.0;
		a = 63.0 - a;
	}
	else
	{
		a = 0.0;
	}

	float fResult = cos(a*(3.1415926535*2.0)/128.0+3.1415926535)*31.0+32.0;
	fResult /= 63.0;

	return fResult;
}
float Palette3_GetGreen(float fIndex)
{
	float a = 0.0;
	if ((fIndex >= 0.0) && (fIndex < 64.0))
	{
		a = 0.0;
	}
	else if ((fIndex >= 64.0) && (fIndex < 128.0))
	{
		a = fIndex - 64.0;
	}
	else if ((fIndex >= 128.0) && (fIndex < 192.0))
	{
		a = fIndex - 128.0;
		a = 63.0 - a;
	}
	else
	{
		a = 0.0;
	}

	float fResult = cos(a*(3.1415926535*2.0)/128.0+3.1415926535)*31.0+32.0;
	fResult /= 63.0;

	return fResult;
}
float Palette3_GetBlue(float fIndex)
{
	float a = 0.0;
	if ((fIndex >= 0.0) && (fIndex < 64.0))
	{
		a = 0.0;
	}
	else if ((fIndex >= 64.0) && (fIndex < 128.0))
	{
		a = fIndex - 64.0;
	}
	else if ((fIndex >= 128.0) && (fIndex < 192.0))
	{
		a = 63.0;
	}
	else
	{
		a = 63.0;
	}

	float fResult = cos(a*(3.1415926535*2.0)/128.0+3.1415926535)*31.0+32.0;
	fResult /= 63.0;

	return fResult;
}

float Palette4_GetRed(float fIndex)
{
	float a = 0.0;
	if ((fIndex >= 0.0) && (fIndex < 75.0))
	{
		a = fIndex;
		a = 63.0 - a * 64.0 / 75.0;
	}
	else if ((fIndex >= 75.0) && (fIndex < 181.0))
	{
		return 0.0;
	}
	else
	{
		a = fIndex - 181.0;
		a = (a * 64.0 / 75.0) * 8.0 / 10.0;
	}

	float fResult = cos(a*(3.1415926535*2.0)/128.0+3.1415926535)*31.0+32.0;
	fResult /= 63.0;

	return fResult;
}
float Palette4_GetGreen(float fIndex)
{
	float a = 0.0;
	if ((fIndex >= 0.0) && (fIndex < 75.0))
	{
		a = fIndex;
		a = 63.0 - a * 64.0 / 75.0;
	}
	else if ((fIndex >= 75.0) && (fIndex < 181.0))
	{
		return 0.0;
	}
	else
	{
		a = fIndex - 181.0;
		a = (a * 64.0 / 75.0) * 9.0 / 10.0;
	}

	float fResult = cos(a*(3.1415926535*2.0)/128.0+3.1415926535)*31.0+32.0;
	fResult /= 63.0;

	return fResult;
}
float Palette4_GetBlue(float fIndex)
{
	float a = 0.0;
	if ((fIndex >= 0.0) && (fIndex < 75.0))
	{
		a = fIndex;
		a = 63.0 - a * 64.0 / 75.0;
	}
	else if ((fIndex >= 75.0) && (fIndex < 181.0))
	{
		return 0.0;
	}
	else
	{
		a = fIndex - 181.0;
		a = (a * 64.0 / 75.0);
	}

	float fResult = cos(a*(3.1415926535*2.0)/128.0+3.1415926535)*31.0+32.0;
	fResult /= 63.0;

	return fResult;
}

float Palette_GetRed(float fIndex, int t)
{
	int nPlasmaIndex = GetPlasmaIndex(t);
	float fResult = 0.0;

	if (nPlasmaIndex == 0)
	{
		fResult = Palette0_GetRed(fIndex);
	}
	else if (nPlasmaIndex == 1)
	{
		fResult = Palette1_GetRed(fIndex);
	}
	else if (nPlasmaIndex == 2)
	{
		fResult = Palette2_GetRed(fIndex);
	}
	else if (nPlasmaIndex == 3)
	{
		fResult = Palette3_GetRed(fIndex);
	}
	else if (nPlasmaIndex == 4)
	{
		fResult = Palette4_GetRed(fIndex);
	}

	return fResult;
}
float Palette_GetGreen(float fIndex, int t)
{
	int nPlasmaIndex = GetPlasmaIndex(t);
	float fResult = 0.0;

	if (nPlasmaIndex == 0)
	{
		fResult = Palette0_GetGreen(fIndex);
	}
	else if (nPlasmaIndex == 1)
	{
		fResult = Palette1_GetGreen(fIndex);
	}
	else if (nPlasmaIndex == 2)
	{
		fResult = Palette2_GetGreen(fIndex);
	}
	else if (nPlasmaIndex == 3)
	{
		fResult = Palette3_GetGreen(fIndex);
	}
	else if (nPlasmaIndex == 4)
	{
		fResult = Palette4_GetGreen(fIndex);
	}

	return fResult;
}
float Palette_GetBlue(float fIndex, int t)
{
	int nPlasmaIndex = GetPlasmaIndex(t);
	float fResult = 0.0;

	if (nPlasmaIndex == 0)
	{
		fResult = Palette0_GetBlue(fIndex);
	}
	else if (nPlasmaIndex == 1)
	{
		fResult = Palette1_GetBlue(fIndex);
	}
	else if (nPlasmaIndex == 2)
	{
		fResult = Palette2_GetBlue(fIndex);
	}
	else if (nPlasmaIndex == 3)
	{
		fResult = Palette3_GetBlue(fIndex);
	}
	else if (nPlasmaIndex == 4)
	{
		fResult = Palette4_GetBlue(fIndex);
	}

	return fResult;
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	// t is an integer frame count.
	int t = int(iTime * 60.0);

	// fPlasmaX is a float from 0 to 319,
	// fPlasmaY is a float from 0 to 399.
	float fPlasmaX = (fragCoord.x / iResolution.x) * 319.0;
	float fPlasmaY = (fragCoord.y / iResolution.y) * 399.0;
	fPlasmaY = 399.0 - fPlasmaY;

	float fRed = 0.0;
	float fGreen = 0.0;
	float fBlue = 0.0;

	float fScreenX = fragCoord.x;
	float fScreenY = fragCoord.y;
	float ah = 0.0;
	float ccc = fPlasmaX / 4.0;
	float fOffset = 0.0;
	float bx = 0.0;

	int nXOffset0or1 = int(mod(fScreenX, 2.0));
	// On every odd row, toggle the x offset. This causes different plasma constants to be
	// read for these pixels, achieving a chequered overlay of two plasmas.
	int xor = int(mod(fScreenY, 2.0));
	if (xor == 1)
	{
		if (nXOffset0or1 == 0)
		{
			nXOffset0or1 = 1;
		}
		else
		{
			nXOffset0or1 = 0;
		}
	}

	float c1 = float(GetC1(nXOffset0or1, t));
	float c2 = float(GetC2(nXOffset0or1, t));
	float c3 = float(GetC3(nXOffset0or1, t));
	float c4 = float(GetC4(nXOffset0or1, t));

	fOffset = ((fPlasmaY * 2.0) + (c2 * 2.0) - (ccc * 8.0) + (80.0 * 8.0)) / 2.0;
	bx = ((sin(fOffset*DPII/4096.0)*55.0+sin(fOffset*DPII/4096.0*4.0)*5.0+sin(fOffset*DPII/4096.0*17.0)*3.0+64.0)*16.0);

	fOffset = bx + c1 + (ccc * 8.0);
	ah = (sin(fOffset*DPII/4096.0)*55.0+sin(fOffset*DPII/4096.0*6.0)*5.0+sin(fOffset*DPII/4096.0*21.0)*4.0+64.0);

	fOffset = ((fPlasmaY * 2.0) + (c4 * 2.0) + (ccc * 32.0)) / 2.0;
	bx = ((sin(fOffset*DPII/4096.0)*55.0+sin(fOffset*DPII/4096.0*5.0)*8.0+sin(fOffset*DPII/4096.0*15.0)*2.0+64.0)*8.0);

	fOffset = bx + (fPlasmaY * 2.0) + c3 - (ccc * 4.0) + (80.0 * 4.0);
	ah += (sin(fOffset*DPII/4096.0)*55.0+sin(fOffset*DPII/4096.0*6.0)*5.0+sin(fOffset*DPII/4096.0*21.0)*4.0+64.0);

	float fPaletteIndex = ah;

	fRed = Palette_GetRed(fPaletteIndex, t);
	fGreen = Palette_GetGreen(fPaletteIndex, t);
	fBlue = Palette_GetBlue(fPaletteIndex, t);

	fragColor = vec4(fRed, fGreen, fBlue, 1.0);
}
