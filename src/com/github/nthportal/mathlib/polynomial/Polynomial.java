package com.github.nthportal.mathlib.polynomial;

import java.util.ArrayList;
import java.util.Collections;

public class Polynomial
{
	private ArrayList<PolyUnit> terms;

	public Polynomial()
	{
		this.terms = new ArrayList<PolyUnit>();
	}

	public Polynomial(ArrayList<PolyUnit> monomials)
	{
		this.terms = new ArrayList<PolyUnit>();
		for (int i = 0; i < monomials.size(); i++)
		{
			this.add(new PolyUnit(monomials.get(i)));
		}
	}

	public Polynomial(Polynomial p)
	{
		this.terms = new ArrayList<PolyUnit>();
		for (int i = 0; i < p.terms.size(); i++)
		{
			this.terms.add(new PolyUnit(p.terms.get(i)));
		}
	}

	// Creates a Polynomial with one term
	public Polynomial(double coefficient, int exponent)
	{
		if (exponent < 0)
		{
			throw new InvalidExponentException();
		}

		this.terms = new ArrayList<PolyUnit>();
		this.terms.add(new PolyUnit(coefficient, exponent));
	}

	public PolyUnit getTerm(int index)
	{
		return new PolyUnit(terms.get(index));
	}

	public int getSize()
	{
		return this.terms.size();
	}

	/*
	 * private void reduce() { Polynomial temp = new Polynomial(this);
	 * Polynomial temp2 = new Polynomial();
	 * 
	 * this.terms.clear();
	 * 
	 * for (int i = 0; i < temp.terms.size(); i++) { temp2 =
	 * temp2.add(temp.terms.get(i)); }
	 * 
	 * this.terms = temp2.terms; }
	 */

	private void order()
	{
		Collections.sort(this.terms, Collections.reverseOrder());
	}

	public Polynomial add(PolyUnit m)
	{
		Polynomial result = new Polynomial(this);

		// Returns original polynomial if monomial added has a 0 coefficient
		if (m.getCoefficient() == 0)
		{
			return result;
		}

		PolyUnit temp;

		for (int i = 0; i < result.terms.size(); i++)
		{
			if (m.getExponent() == result.terms.get(i).getExponent())
			{
				temp = result.terms.get(i).add(m);
				if (temp.getCoefficient() == 0)
				{
					result.terms.remove(i);
				}
				else
				{
					result.terms.set(i, temp);
				}
				return result;
			}
		}
		// Else
		result.terms.add(m);
		result.order();
		return result;
	}

	public Polynomial add(Polynomial p)
	{
		Polynomial result = new Polynomial(this);

		for (int i = 0; i < p.terms.size(); i++)
		{
			result = result.add(p.terms.get(i));
		}

		return result;
	}

	public Polynomial subtract(PolyUnit m)
	{
		Polynomial result = new Polynomial(this);

		// Returns original polynomial if monomial subtracted has a 0
		// coefficient
		if (m.getCoefficient() == 0)
		{
			return result;
		}

		PolyUnit temp;

		for (int i = 0; i < result.terms.size(); i++)
		{
			if (m.getExponent() == result.terms.get(i).getExponent())
			{
				temp = result.terms.get(i).subtract(m);
				if (temp.getCoefficient() == 0)
				{
					result.terms.remove(i);
				}
				else
				{
					result.terms.set(i, temp);
				}
				return result;
			}
		}
		// Else
		m = m.multiply(-1);
		result.terms.add(m);
		result.order();
		return result;
	}

	public Polynomial subtract(Polynomial p)
	{
		Polynomial result = new Polynomial(this);

		for (int i = 0; i < p.terms.size(); i++)
		{
			result = result.subtract(p.terms.get(i));
		}

		return result;
	}

	public Polynomial multiply(double scalar)
	{
		Polynomial result = new Polynomial();

		for (int i = 0; i < this.terms.size(); i++)
		{
			result.terms.add(this.terms.get(i).multiply(scalar));
		}

		return result;
	}

	private Polynomial multiply(PolyUnit m)
	{
		Polynomial result = new Polynomial();

		for (int i = 0; i < this.terms.size(); i++)
		{
			result.terms.add(this.terms.get(i).multiply(m));
		}

		return result;
	}

	public Polynomial multiply(Polynomial p)
	{
		Polynomial result = new Polynomial();

		for (int i = 0; i < this.terms.size(); i++)
		{
			for (int j = 0; j < p.terms.size(); j++)
			{
				result = result.add(this.terms.get(i).multiply(p.terms.get(j)));
			}
		}

		return result;
	}

	public Polynomial divide(double scalar)
	{
		Polynomial result = new Polynomial();

		for (int i = 0; i < this.terms.size(); i++)
		{
			result.terms.add(this.terms.get(i).divide(scalar));
		}

		return result;
	}

	// Returns array of 2 FractionPolynomials: quotient then remainder
	public Polynomial[] divide(Polynomial divisor)
	{
		Polynomial[] result = new Polynomial[2];
		result[0] = new Polynomial();
		result[1] = new Polynomial();

		// Check for divisor or dividend being empty polynomial (equivalent to
		// 0)
		if (divisor.terms.isEmpty())
		{
			throw new ZeroDivisionException(
					"Cannot divide polynomial by a polynomial with value of 0.");
		}

		Polynomial dividend = new Polynomial(this);
		PolyUnit firstDividendTerm;
		PolyUnit firstDivisorTerm;
		PolyUnit temp = new PolyUnit();

		while (true)
		{
			if (dividend.terms.isEmpty())
			{
				return result;
			}

			firstDividendTerm = dividend.terms.get(0);
			firstDivisorTerm = divisor.terms.get(0);

			// Check if dividing by higher order term
			if (firstDividendTerm.compareTo(firstDivisorTerm) == -1)
			{
				result[1] = new Polynomial(dividend);
				return result;
			}

			temp = firstDividendTerm.divide(firstDivisorTerm);
			result[0].terms.add(temp);

			dividend = dividend.subtract(divisor.multiply(temp));
		}
	}

	public boolean equals(Polynomial p)
	{
		int size = this.terms.size();

		if (size != p.terms.size())
		{
			return false;
		}

		// Should work because polynomial should always be sorted
		for (int i = 0; i < size; i++)
		{
			if (!this.terms.get(i).equals(p.terms.get(i)))
			{
				return false;
			}
		}

		// Else
		return true;
	}

	public double eval(double value)
	{
		double result = 0;

		for (int i = 0; i < this.terms.size(); i++)
		{
			result += this.terms.get(i).eval(value);
		}

		return result;
	}

	public Polynomial derivative()
	{
		Polynomial result = new Polynomial();
		PolyUnit temp;

		for (int i = 0; i < this.terms.size(); i++)
		{
			temp = this.terms.get(i).derivative();
			if (temp.getCoefficient() != 0)
			{
				result.terms.add(temp);
			}
		}

		return result;
	}

	public Polynomial antiDerivative()
	{
		Polynomial result = new Polynomial();

		for (int i = 0; i < this.terms.size(); i++)
		{
			result.terms.add(this.terms.get(i).antiDerivative());
		}

		return result;
	}

	public Polynomial integral(double constant)
	{
		Polynomial result = new Polynomial();

		for (int i = 0; i < this.terms.size(); i++)
		{
			result.terms.add(this.terms.get(i).antiDerivative());
		}
		result.terms.add(new PolyUnit(constant, 0));

		return result;
	}

	public double integral(double lowerBound, double upperBound)
	{
		double result = 0;

		for (int i = 0; i < this.terms.size(); i++)
		{
			result += this.terms.get(i).integral(lowerBound, upperBound);
		}

		return result;
	}

	public void print()
	{
		int size = this.terms.size();

		for (int i = 0; i < size; i++)
		{
			this.terms.get(i).print();
			if (i != (size - 1))
			{
				System.out.print(" ");
			}
		}
	}

	public void println()
	{
		int size = this.terms.size();

		for (int i = 0; i < size; i++)
		{
			this.terms.get(i).print();
			if (i != (size - 1))
			{
				System.out.print(" ");
			}
		}

		System.out.println();
	}

	public static void main(String[] args)
	{
		Polynomial dividend = new Polynomial();
		Polynomial divisor = new Polynomial();

		dividend = dividend.add(new PolyUnit(4, 5));
		dividend = dividend.add(new PolyUnit(-3, 4));
		dividend = dividend.add(new PolyUnit(1, 3));
		dividend = dividend.add(new PolyUnit(-1, 1));
		dividend = dividend.add(new PolyUnit(6, 0));

		divisor = divisor.add(new PolyUnit(2, 2));
		divisor = divisor.add(new PolyUnit(-1, 1));
		divisor = divisor.add(new PolyUnit(3, 0));

		System.out.print("Dividend: ");
		dividend.println();

		System.out.print("Divisor: ");
		divisor.println();

		Polynomial[] result = dividend.divide(divisor);

		System.out.print("Quotient: ");
		result[0].println();

		System.out.print("Remainder: ");
		result[1].println();
	}
}