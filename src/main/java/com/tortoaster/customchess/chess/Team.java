package com.tortoaster.customchess.chess;

public enum Team {
	LIGHT(1, "l_"),
	DARK(-1, "d_");
	
	public static final String SUFFIX = ".png";
	
	private final int coefficient;
	
	private final String prefix;
	
	Team(int coefficient, String prefix) {
		this.coefficient = coefficient;
		this.prefix = prefix;
	}
	
	public int getCoefficient() {
		return coefficient;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public Team getOppositeTeam() {
		if(this == DARK) return LIGHT;
		return DARK;
	}
	
	public Team getNextTeam() {
		if(this == DARK) return LIGHT;
		return DARK;
	}
	
	public static Team getStartTeam() {
		return LIGHT;
	}
}