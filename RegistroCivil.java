/**
 * Práctica 4. Archivos de texto, cadenas, excepciones
 *
 * @author Ervey Guerrero Gómez
 * @author David Hernandéz López
 * @author Daniel Sánchez Vázquez 
 * @author Alejandro Tonatiuh García Espinoza
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Demuestra el funcionamiento del programa.
 */
public class RegistroCivil {
	public static void main(String[] args) {
		CiudadanoDao ciudadanoDao = new CiudadanoDaoImp();

		ciudadanoDao.cargarActas();
		ciudadanoDao.guardarCiudadanos();
	}
}

/**
 * Define las operaciones estándar a realizar con
 * la clase Ciudadano. 
 *
 * Data Access Object Interface
 */
interface CiudadanoDao {
	/**
	 * Lee los datos almacenados en el fichero "registrosCiudadanos.txt".
	 */
	public void cargarActas();
	/**
	 * Crea una copia de los ciudadanos almacenados y ordena a todos
	 * los ciudadanos por apellido paterno.
	 *
	 * @return la copia ordenada de los ciudadanos.
	 */
	public ArrayList<Ciudadano> getCiudadanosOrdenados();
	/**
	 * Obtiene a todos los ciudadanos que hayan nacido
	 * en un estado en especifico.
	 *
	 * @param estado el estado que cuyos ciudadanos nos interesa obtener.
	 *
	 * @return los ciudadanos nacidos en el estado dado. 
	 */
	public ArrayList<Ciudadano> getCiudadanosPorEstado(String estado);
	/**
	 * Almacena en el fichero "ciudadanosOrdenados.txt" los
	 * datos en formato: 
	 *
	 * [Apellido Paterno] [Apellido Materno] [Nombre], [DD/MM/YYYY], [Sexo]
	 * */
	public void guardarCiudadanos();
}

/** 
 * Implementa las operaciones estándar a realizar con
 * la clase Ciudadano.
 */
class CiudadanoDaoImp implements CiudadanoDao {
	private ArrayList<Ciudadano> ciudadanos;
	private CiudadanoFactory ciudadanoFactory;

	/**
	 * Crea un nuevo objeto de tipo CiudadanoDaoImp.
	 */
	public CiudadanoDaoImp() {
		this.ciudadanos = new ArrayList<Ciudadano>();
		this.ciudadanoFactory = new CiudadanoFactory();
	}

	/**
	 * Implementa el método cargarActas(). 
	 */
	@Override
	public void cargarActas() {
		BufferedReader entrada = null;
		String fichero = "registrosCiudadanos.txt";
		String lineaActa, actas = "";

		try {
			entrada = new BufferedReader(new FileReader(fichero));
			while ((lineaActa = entrada.readLine()) != null)
				actas += (lineaActa + "\n");

			this.extraerCiudadanos(actas);

			entrada.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				entrada.close();
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Implementa el método getCiudadanosOrdenados().
	 */
	@Override
	public ArrayList<Ciudadano> getCiudadanosOrdenados() {
		ArrayList<Ciudadano> ciudadanosOrdenados = this.ciudadanos;
		Ciudadano aux;
		int numCiudadanos = ciudadanosOrdenados.size();
		for (int i = 0; i < numCiudadanos; i++)
			for (int j = 0; j < numCiudadanos; j++)
				if (ciudadanosOrdenados.get(i).getApellidoPaterno()
						.compareToIgnoreCase(ciudadanosOrdenados.get(j)
							.getApellidoPaterno()) < 0) {
					aux = ciudadanosOrdenados.get(i);
					ciudadanosOrdenados.set(i, ciudadanosOrdenados.get(j));
					ciudadanosOrdenados.set(j, aux);
				}
		return ciudadanosOrdenados;
	}

	/**
	 * Implementa el método getCiudadanosPorEstado().
	 */
	@Override
	public ArrayList<Ciudadano> getCiudadanosPorEstado(String estado) {
		ArrayList<Ciudadano> cPorEstado = new ArrayList<Ciudadano>();
		for (Ciudadano ciudadano: this.ciudadanos)
			if (ciudadano.getEstadoNacimiento().equalsIgnoreCase(estado))
				cPorEstado.add(ciudadano);
		return cPorEstado;
	}

	/**
	 * Implementa el método guardarCiudadanos().
	 *
	 * {@link} https://docs.oracle.com/javase/8/docs/technotes/guides/language/foreach.html
	 */
	@Override
	public void guardarCiudadanos() {
		PrintWriter salida = null;
		String fichero = "ciudadanosOrden.txt";
		ArrayList<Ciudadano> ciudadanosOrdenados = this.getCiudadanosOrdenados();

		try {
			salida = new PrintWriter(new BufferedWriter(new FileWriter(fichero)));
			for (Ciudadano ciudadano: ciudadanosOrdenados)
				salida.println(ciudadano);
			for (EnumEstado e: EnumEstado.values()) {
				String nomEstado = e.getNombre();
				int nCiudadanosEstado = this.getCiudadanosPorEstado(nomEstado).size();
				if (nCiudadanosEstado > 0)
					salida.println(nomEstado + " " + nCiudadanosEstado);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			salida.close();
		}
	}

	/**
	 * Separa los datos de los distintos ciudadanos que se pudieran encontran
	 * en el fichero para crear un objeto de Ciudadano con dichos datos, para
	 * almacenarlo.
	 *
	 * @param actas Texto almacenado en el fichero.
	 *
	 * @see java.util.ArrayList
	 */
	private void extraerCiudadanos(String actas) {
		String acta = "";
		Ciudadano ciudadano;
		int nSaltos = 0;

		for (char chr: actas.toCharArray()) {
			acta += chr;
			if (chr == '\n')
				nSaltos++;
			if (nSaltos == 5) {
				nSaltos = 0;
				 ciudadano = this.ciudadanoFactory.getCiudadano(acta);
				if (ciudadano != null) {
					this.ciudadanos.add(ciudadano);
				}
				acta = "";
			}
		}
	}
}

/**
 *  Se encarga de la lógica para crear un nuevo objeto de tipo
 *  Ciudadano.
 */
class CiudadanoFactory {
	/**
	 * Crea un nuevo Ciudadano a partir de los datos de un "acta" con
	 * formato especifico.
	 *
	 * @param acta el acta que almacena los datos de un ciudadano.
	 *
	 * @return un Ciudadano con los datos del acta. 
	 * 
	 * @see java.util.regex.Matcher
	 * @see java.util.regex.Pattern
	 */
	public Ciudadano getCiudadano(String acta) {
		String regex, nombre, apellidoPaterno, apellidoMaterno, estadoNacimiento, fechaNacimiento, sexo;
		Matcher comparador;
		Pattern patron;

		try {
			regex = "( (\\w*) (\\w*) (\\w*))";
			patron = Pattern.compile(regex);
			comparador = patron.matcher(acta);
			comparador.find();
			nombre = comparador.group(2);
			apellidoPaterno = comparador.group(3);
			apellidoMaterno = comparador.group(4);
			
			regex = "\\d{1,2}\\s\\w{4,10}\\s\\d{4}";
			patron = Pattern.compile(regex);
			comparador = patron.matcher(acta);
			comparador.find();
			fechaNacimiento = comparador.group();

			regex = "\\w{5,6}(INO)";
			patron = Pattern.compile(regex);
			comparador = patron.matcher(acta);
			comparador.find();
			sexo = comparador.group().toLowerCase();

			regex = "((\\w*), (\\w*), (\\w*))";
			patron = Pattern.compile(regex);
			comparador = patron.matcher(acta);
			comparador.find();
			estadoNacimiento = comparador.group(3);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return new Ciudadano(StringHelper.capitalize(nombre),
			       	StringHelper.capitalize(apellidoPaterno),
				StringHelper.capitalize(apellidoMaterno),
				estadoNacimiento,	
				FechaHelper.format(fechaNacimiento),
			       	sexo);
	}
}

/**
 * Representa a un ciudadano, almacenando y proveyendo métodos para el uso de 
 * los datos extraídos por la clase DAO.  
 */
class Ciudadano {
	private String nombre;
	private String apellidoPaterno;
	private String apellidoMaterno;
	private String estadoNacimiento;
	private String fechaNacimiento;
	private String sexo;

	/**
	 * Crea un nuevo objeto de tipo Ciudadano.
	 *
	 * @param nombre el nombre del ciudadano.
	 * @param apellidoPaterno el apellido paterno del ciudadano.
	 * @param apellidoMaterno el apellido materno del ciudadano.
	 * @param estadoNacimiento el estado de nacimiento del ciudadano.
	 * @param fechaNacimiento la fecha de nacimiento del ciudadano.
	 * @param sexo el sexo del ciudadano.
	 */
	public Ciudadano(String nombre,
			String apellidoPaterno,
			String apellidoMaterno,
			String estadoNacimiento,
			String fechaNacimiento,
			String sexo) {
		this.nombre = nombre;
		this.apellidoPaterno = apellidoPaterno;
		this.apellidoMaterno = apellidoMaterno;
		this.estadoNacimiento = estadoNacimiento;
		this.fechaNacimiento = fechaNacimiento;
		this.sexo = sexo;
	}

	/**
	 * Getter para el atributo nombre.
	 *
	 * @return el nombre del ciudadano.
	 */
	public String getNombre() {
		return this.nombre;
	}

	/**
	 * Getter para el atributo apellidoPaterno.
	 *
	 * @return el apellido paterno del ciudadano.
	 */
	public String getApellidoPaterno() {
		return this.apellidoPaterno;
	}

	/**
	 * Getter para el atributo apellidoMaterno.
	 *
	 * @return el apellido materno del ciudadano.
	 */
	public String getApellidoMaterno() {
		return this.apellidoMaterno;
	}

	/**
	 * Getter para el atributo estadoNacimiento.
	 *
	 * @return el estado de nacimiento del ciudadano.
	 */
	public String getEstadoNacimiento() {
		return this.estadoNacimiento;
	}

	/**
	 * Getter para el atributo fechaNacimiento.
	 *
	 * @return la fecha de nacimiento del ciudadano.
	 */
	public String getFechaNacimiento() {
		return this.fechaNacimiento;
	}

	/**
	 * Getter para el atributo sexo.
	 *
	 * @return el sexo del ciudadano.
	 */
	public String getSexo() {
		return this.sexo;
	}

	/**
	 * Sobreescribe el método toString().
	 *
	 * @return una cadena representando el objeto. 
	 *
	 * @see Object.toString()
	 */
	@Override
	public String toString() {
		return String.format("%s %s %s, %s, %s",
				this.getApellidoPaterno(),
				this.getApellidoMaterno(),
				this.getNombre(),
				this.getFechaNacimiento(),
				this.getSexo());
	}
}

/**
 * Provee métodos de utilería de cadenas.
 */
class StringHelper {
	/**
	 * Vuelve todas las letras de una cadena a minúsculas, excepto la primera
	 * letra, la cual deja en mayúsculas.
	 *
	 * @param cad la cadena a dar formato.
	 *
	 * @return la cadena con el formato deseado.
	 */
	public static String capitalize(String cad) {
		return cad.substring(0, 1).toUpperCase() + cad.substring(1).toLowerCase();
	}
}

/**
 * Provee métodos de utilería con cadenas representando fechas.
 */
class FechaHelper {
	/**
	 * Cambia el formato de una fecha de: "dd MMMM yyyy"
	 * a: "dd/MM/yyyy". 
	 *
	 * @param fecha la fecha a dar formato.
	 *
	 * @return la fecha con el formato "adecuado".
	 */
	public static String format(String fecha) {
		String regex, dia, mes, año;
		Matcher comparador;
		Pattern patron;

		try {
			regex = "((\\d*) (\\w*) (\\d*))";
			patron = Pattern.compile(regex);
			comparador = patron.matcher(fecha);
			comparador.find();
			dia = comparador.group(2);
			mes = comparador.group(3);
			año = comparador.group(4);
		} catch (Exception e) {
			e.printStackTrace();
			return "00/00/00";
		}

		return String.format("%s/%s/%s", dia, FechaHelper.parseMonth(mes), año);
	}

	/**
	 * Obtiene el número de mes del año por el nombre del mes.
	 *
	 * @param month el nombre del mes.
	 *
	 * @return el número del mes con el formato "adecuado".
	 *
	 * @see String.format()
	 */
	public static String parseMonth(String month) {
		try {
			return String.format("%02d", EnumMes.valueOf(month).getNumMes());
		} catch (Exception e) {
			return "00";
		}
	}
}

/**
 * Representa los meses del año.
 *
 * @see java.util.Enumeration
 */
enum EnumMes {
	ENERO(1,"ENERO"),
	FEBRERO(2,"FEBRERO"),
	MARZO(3,"MARZO"),
	ABRIL(4,"ABRIL"),
	MAYO(5,"MAYO"),
	JUNIO(6,"JUNIO"),
	JULIO(7,"JULIO"),
	AGOSTO(8,"AGOSTO"),
	SEPTIEMBRE(9,"SEPTIEMBRE"),
	OCTUBRE(10,"OCTUBRE"),
	NOVIEMBRE(11,"NOVIEMBRE"),
	DICIEMBRE(12,"DICIEMBRE");

	private String nombre;
	private int numMes;

	/**
	 * Crea un nuevo valor de la enumeración.
	 *
	 * @param numMes el número del mes.
	 * @param nombre el nombre del mes.
	 */
	private EnumMes(int numMes, String nombre) {
		this.numMes = numMes;
		this.nombre = nombre;
	}

	/**
	 * Getter para el atributo numMes.
	 *
	 * @return el número de mes.
	 */
	public int getNumMes() {
		return this.numMes;
	}

	/**
	 * Getter para el atributo nombre.
	 *
	 * @return el nombre del mes.
	 */
	public String getNombre() {
		return this.nombre;
	}
}

/*
 * Representa los estados de la republica mexicana.
 */
enum EnumEstado {
	AGUASCALIENTES(0, "Aguascalientes"),
	BAJA_CALIFORNIA(1, "Baja California"),
	BAJA_CALIFORNIA_SUR(2, "Baja California Sur"),
	CAMPECHE(3, "Campeche"),
	CHIAPAS(4, "Chiapas"),
	CHIHUAHUA(5, "Chihuahua"),
	CIUDAD_DE_MEXICO(6, "Ciudad de México"),
	COAHUILA(7, "Coahuila de Zaragoza"),
	COLIMA(8, "Colima"),
	DURANGO(9, "Durango"),
	GUANAJUATO(10, "Guanajuato"),
	GUERRERO(11, "Guerrero"),
	HIDALGO(12, "Hidalgo"),
	JALISCO(13, "Jalisco"),
	MEXICO(14, "México"),
	MICHOACAN(15, "Michoacán de Ocampo"),
	MORELOS(16, "Morelos"),
	NAYARIT(17, "Nayarit"),
	NUEVO_LEON(18, "Nuevo León"),
	OAXACA(19, "Oaxaca"),
	PUEBLA(20, "Puebla"),
	QUERETARO(21, "Queretaro"),
	QUINTANA_ROO(22, "Quintana Roo"),
	SAN_LUIS_POTOSI(23, "San Luis Potosí"),
	SINALOA(24, "Sinaloa"),
	SONORA(25, "Sonora"),
	TABASCO(26, "Tabasco"),
	TAMAULIPAS(27, "Tamaulipas"),
	TLAXCALA(28, "Tlaxcala"),
	VERACTRUZ(29, "Veracruz"),
	YUCATAN(30, "Yucatán"),
	ZACATECAS(31, "Zacatecas");

	private final int id;
	private final String nombre;

	/**
	 * Crea un nuevo valor de la enumeración.
	 * 
	 * @param id el id del estado.
	 * @param nombre el nombre del estado.
	 */
	private EnumEstado(int id, String nombre) {
		this.id = id;
		this.nombre = nombre;
	}

	/** 
	 * Obtiene el estado con id dado.
	 *
	 * @param id el id del estado.
	 *
	 * @return el valor de la enumeración con el id dado.
	 */
	public static EnumEstado getEstadoPorId(int id) {
		for (EnumEstado e: EnumEstado.values()) {
			if (e.getId() == id) return e;
		}
		return null;
	}

	/**
	 * Getter para el atributo id.
	 *
	 * @return el id del estado.
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Getter para el atributo nombre.
	 *
	 * @return el nombre del estado.
	 */
	public String getNombre() {
		return this.nombre;
	}

}
