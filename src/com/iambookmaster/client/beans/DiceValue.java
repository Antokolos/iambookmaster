package com.iambookmaster.client.beans;

import com.iambookmaster.client.common.Dice;

public class DiceValue {

	private static final long serialVersionUID = 1L;
	
	private int size=6;
	private int n=1;
	private int constant=0;

	private boolean fatal;

	public DiceValue(String dice) {
		if (dice != null) {
			String[] vals = dice.split(",");
			if (vals.length==3) {
				try {
					n = Integer.parseInt(vals[0]);
					size = Integer.parseInt(vals[1]);
					constant = Integer.parseInt(vals[2]);
				} catch (NumberFormatException e) {
				}
			}
		}
	}

	public DiceValue() {
	}


	public DiceValue(int size, int n, int constant) {
		this.size = size;
		this.n = n;
		this.constant = constant;
	}

	public DiceValue(DiceValue heroInitialValue) {
		this.size = heroInitialValue.size;
		this.n = heroInitialValue.n;
		this.constant = heroInitialValue.constant;
	}

	public String getJSON() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(n);
		buffer.append(',');
		buffer.append(size);
		buffer.append(',');
		buffer.append(constant);
		return buffer.toString();
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public int getConstant() {
		return constant;
	}

	public void setConstant(int constant) {
		this.constant = constant;
	}

	public int calculate() {
		if (n != 0 && size>0) {
			if (n==1) {
				int val = Dice.drop(size);
				fatal=(val==size);
				return constant+val;
			} else if (n==-1){
				int val = Dice.drop(size);
				fatal=(val==size);
				return constant-val;
			} else if (n>0){
				int val=0;
				fatal=true;
				for (int i = 0; i < n; i++) {
					int dice = Dice.drop(size);
					if (val<size) {
						fatal = false;
					}
					val = val + dice;
				}
				return val;
			} else {
				int val=0;
				fatal=true;
				for (int i = n; i < 0; i++) {
					int dice = Dice.drop(size);
					if (val<size) {
						fatal = false;
					}
					val = val + dice;
				}
				return -val;
			}
		} else {
			fatal=false;
			return constant;
		}
	}

	public boolean isFatal() {
		return fatal;
	}

	@Override
	public String toString() {
		if (constant==0 && (n==0 || size==0)) {
			return "0";
		}
		StringBuilder builder = new StringBuilder();
		if (constant != 0) {
			builder.append(constant);
			if (size !=0 && n!=0) {
				if (n>0) {
					builder.append('+');
				} else {
					builder.append('-');
				}
				builder.append(n);
				builder.append('D');
				builder.append(size);
			}
		} else {
			if (size !=0 && n!=0) {
				if (n<0) {
					builder.append('-');
				}
				builder.append(n);
				builder.append('D');
				builder.append(size);
			}
		}
		return builder.toString();
	}

	public boolean isZero() {
		return constant==0 && (n==0 || size==0);
	}

	public boolean isPlus() {
		return constant>0 || (constant==0 && n>0);
	}

	public String toAbsString() {
		if (constant==0 && (n==0 || size==0)) {
			return "0";
		}
		StringBuilder builder = new StringBuilder();
		if (constant != 0) {
			if (constant > 0) {
				builder.append('-');
			}
			builder.append(Math.abs(constant));
			if (size !=0 && n!=0) {
				if (n>0) {
					builder.append('+');
					builder.append(n);
				} else {
					builder.append('-');
					builder.append(n);
				}
				builder.append('D');
				builder.append(size);
			}
		} else {
			if (size !=0 && n!=0) {
				if (n>0) {
					builder.append('-');
					builder.append(n);
				} else {
					builder.append(Math.abs(n));
				}
				builder.append('D');
				builder.append(size);
			}
		}
		return builder.toString();
	}

	public boolean isNoDice() {
		return size==0 || n==0;
	}

	public String getDiceStr() {
		return new StringBuilder().append(n).append('D').append(size).toString();
	}

}
