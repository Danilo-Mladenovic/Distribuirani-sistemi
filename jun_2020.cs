<services>
  <service name="Jun2020.Loto">
  	<endpoint contract="Jun2020.ILoto" binding="wcfDualHttpBinding" />
  </service>
</services>


namespace Jun2020
{
	[ServiceContract(CallbackContract=typeof(ILotoCallback),SessionMode=SessionMode.Required)]
	public interface ILoto
	{
		[OperationContract(IsOnWay=false)]
		void UnesiKombinaciju(string nadimak);

		[OperationContract(IsOnWay=false)]
		void ObrisiKombinaciju(string nadimak);

		[OperationContract(IsOnWay=true)]
		int PosaljiBroj(string nadimak, int broj);
	}

	[DataContract]
	public class Korisnik
	{
		[DataMember]
		public string Nadimak { get; set; }
		
		[DataMember]
		public ILotoCallback Cb = { get; set; }
		
		[DataMember]
		public Dictionary<int, Kombinacija> Kombinacije { get; set; }

		public Korisnik()
		{
			Kombinacije = new Dictionary<int, Kombinacija>();
		}
	}

	[DataContract]
	public class Kombinacija
	{
		[DataMember]
		public int ID { get; set; }

		[DataMember]
		List<int> Brojevi { get; set; }

		public Kombinacija()
		{
			Brojevi = new List<int>();
		}
	}

	public interface ILotoCallback
	{
		[OperationContract(IsOnWay=true)]
		void NoviBroj(int broj);

		[OperationContract(IsOnWay=true)]
		void KrajIgre(int petice, int sestice, int sedmice);

		[OperationContract(IsOnWay=true)]
		void Cestitke(string cestitka);
	}


	[ServiceBehavior(InstanceContextMode=InstanceContextMode.Single)]
	public class Loto : ILoto
	{
		private Dictionary<string, Korisnik> _korisnici;
		private List<int> _izvuceni_brojevi;

		public Loto()
		{
			_korisnici = new Dictionary<string, Korisnik>();
			_izvuceni_brojevi = new List<int>();
		}

		public void UnesiKombinaciju(string nadimak);

		public void ObrisiKombinaciju(string nadimak);

		public int PosaljiBroj(string nadimak, int broj);
	}
}