


namespace Oktobar2020
{
	[ServiceContract(CallbackContract=typeof(ICalculatorCallback), SessionMode=SessionMode.Required)]
	public interface ICalculator
	{
		[OperationContract(IsOneWay=true)]
		void ObrisiRacunanje();

		[OperationContract(IsOneWay=true)]
		void Dodaj(decimal br);

		[OperationContract(IsOneWay=true)]
		void Oduzmi(decimal br);

		[OperationContract(IsOneWay=true)]
		void Pomnozi(decimal br);

		[OperationContract(IsOneWay=true)]
		void Podeli(decimal br);

		[OperationContract(IsOneWay=false)]
		string Izraz();
	}

	[DataContract]
	public class Rezultat
	{
		[DataMember]
		public decimal Rezultat { get; set; }

		[DataMember]
		public string Izraz { get; set; }

		[DataMember]
		public ICalculatorCallback Callback { get; set; }
	}

	public interface ICalculatorCallback
	{
		[OperationContract(IsOneWay=true)]
		void VratiRezultat(Rezultat rez);
	}

	[ServiceBehavior(InstanceContextMode=InstanceContextMode.PerSession)]
	public class Calculator : ICalculator
	{
		private Rezultat Rez { get; set; }

		public Calculator()
		{
			ObrisiRacunanje();
		}

		public void ObrisiRacunanje()
		{
			Rez.Rezultat = 0m;
			Rez.Izraz = "";
			Rez.Callback.VratiRezultat(Rez);
		}

		public void Dodaj(decimal br)
		{
			Rez.Rezultat += br;

			if (String.IsNullOrEmpty(Rez.Izraz))
				Rez.Izraz = br.ToString();
			else
				Rez.Izraz += " + " + br;

			Rez.Callback.VratiRezultat(Rez);
		}

		public void Oduzmi(decimal br)
		{
			Rez.Rezultat -= br;

			if (String.IsNullOrEmpty(Rez.Izraz))
				Rez.Izraz = "-" + br.ToString();
			else
				Rez.Izraz += " - " + br;

			Rez.Callback.VratiRezultat(Rez);
		}

		public void Pomnozi(decimal br)
		{
			Rez.Rezultat *= br;

			if (String.IsNullOrEmpty(Rez.Izraz))
				Rez.Izraz = br.ToString();
			else
				Rez.Izraz += " * " + br;

			Rez.Callback.VratiRezultat(Rez);
		}

		public void Podeli(decimal br)
		{
			if (br == 0)
			{
				ObrisiRacunanje();
				break;
			}

			Rez.Rezultat /= br;

			if (String.IsNullOrEmpty(Rez.Izraz))
				Rez.Izraz = br.ToString();
			else
				Rez.Izraz += " / " + br;

			Rez.Callback.VratiRezultat(Rez);

		}

		public string Izraz()
		{
			if (String.IsNullOrEmpty(Rez.Rezultat))
				return "";
			return Rez.Izraz;
		}
	}
}


namespace Oktobar2020_Client
{
	public partial class FormCalculator : Form, ICalculatorCallback
	{
		private CalculatorClient Proxy { get; set; }

		public FormCalculator()
		{
			InitializeComponent();
			Proxy = new CalculatorClient(new InstanceContext(this));
			textBoxResult.Text = "0";
			textBoxExpression.Text = "";
		}

		public void VratiRezultat(Rezultat rez)
		{
			textBoxResult.Text = rez.Rezultat;
			textBoxExpression.Text = rez.Izraz;
		}

		public void btnClear_Click(object sender, EventArgs e)
		{
			Proxy.ObrisiRacunanje();
		}

		public void btnAdd_Click(object sender, EventArgs e)
		{
			Proxy.Dodaj(decimal.Parse(textInput.Text));
		}

		public void btnSub_Click(object sender, EventArgs e)
		{
			Proxy.Oduzmi(decimal.Parse(textInput.Text));
		}

		public void btnMul_Click(object sender, EventArgs e)
		{
			Proxy.Pomnozi(decimal.Parse(textInput.Text));
		}

		public void btnDiv_Click(object sender, EventArgs e)
		{
			Proxy.Podeli(decimal.Parse(textInput.Text));
		}
	}
}