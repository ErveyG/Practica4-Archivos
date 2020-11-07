package Practica4;

public enum EnumEstado {
    AGUASCALIENTES("1"),
	BAJA_CALIFORNIA("2"),
	BAJA_CALIFORNIA_SUR("3"),
	CAMPECHE("4"),
	CHIAPAS("5"),
	CHIHUAHUA("6"),
	CIUDAD_DE_MEXICO("7"),
	COAHUILA("8"),
	COLIMA("9"),
	DURANGO("10"),
	GUANAJUATO("11"),
	GUERRERO("12"),
	HIDALGO("13"),
	JALISCO("14"),
	MEXICO("15"),
	MICHOACAN("16"),
	MORELOS("17"),
	NAYARIT("18"),
	NUEVO_LEON("19"),
	OAXACA("20"),
	PUEBLA("21"),
	QUERETARO("22"),
	QUINTANA_ROO("23"),
	SAN_LUIS_POTOSI("24"),
	SINALOA("25"),
	SONORA("26"),
	TABASCO("27"),
	TAMAULIPAS("28"),
	TLAXCALA("29"),
	VERACTRUZ("30"),
	YUCATAN("31"),
	ZACATECAS("32");
    public String valor;
    EnumEstado(String valor){
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
    
    
}
