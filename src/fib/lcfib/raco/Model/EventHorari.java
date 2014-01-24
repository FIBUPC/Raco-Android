package fib.lcfib.raco.Model;


public class EventHorari {
	
	private String mAula;
	private String mAssignatura;
	private String mHoraInici;
	private String mHoraFi;
	private int mDia;
	private int mMes;
	private int mAny;
	
	public EventHorari (String horaI, String horaF, String assig, String aula, int dia, int mes, int any){
		this.mAula = aula;
		this.mAssignatura = assig;
		this.mHoraInici = horaI;
		this.mHoraFi = horaF;
		this.mDia = dia;
		this.mMes = mes;
		this.mAny = any;
	}

	/**Per poder crear events quan no hi ha classe*/
	public EventHorari() {
		// TODO Auto-generated constructor stub
	}

	public String getmAula() {
		return mAula;
	}

	public void setmAula(String mAula) {
		this.mAula = mAula;
	}

	public String getmAssignatura() {
		return mAssignatura;
	}

	public void setmAssignatura(String mAssignatura) {
		this.mAssignatura = mAssignatura;
	}

	public String getmHoraInici() {
		return mHoraInici;
	}

	public void setmHoraInici(String mHoraInici) {
		this.mHoraInici = mHoraInici;
	}

	public String getmHoraFi() {
		return mHoraFi;
	}

	public void setmHoraFi(String mHoraFi) {
		this.mHoraFi = mHoraFi;
	}

	
	public int getmDia() {
		return mDia;
	}

	public void setmDia(int mDia) {
		this.mDia = mDia;
	}

	public int getmMes() {
		return mMes;
	}

	public void setmMes(int mMes) {
		this.mMes = mMes;
	}

	public int getmAny() {
		return mAny;
	}

	public void setmAny(int mAny) {
		this.mAny = mAny;
	}
}
