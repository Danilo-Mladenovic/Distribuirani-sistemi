<services> 
  <service name="Jun2019.Prijava">
  	<endpoint contract="Jun2019.IPrijava" binding="basicHttpsBinding" />
  </service>
</services>



namespace Jun2019
{
	[ServiceContract]
	public interface IPrijava
	{
		[OperationContract]
		bool Login(string ime);

		[OperationContract]
		bool Logout(string ime);

		[OperationContract]
		double ProvedenoVreme(string ime);

		[OperationContract]
		List<string> SviKojiSuRadili();
	}

	[DataContract]
	public class Korisnik
	{
		[DataMember]
		public string Ime { get; set; }

		[DataMember]
		public DateTime LoginTime { get; set; }

		[DataMember]
		public DateTime LogoutTime { get; set; }

		[DataMember]
		public bool LoginSuccess { get; set; }

		[DataMember]
		public bool LogoutSuccess { get; set; }
	}


	[ServiceContract(InstanceContextMode=InstanceContextMode.Single)]
	public class Prijava : Iprijava
	{
		private Dictionary<string, Korisnik> _korisnici;

		public Prijava()
		{
			_korisnici = new Dictionary<string, Korisnik>();
		} 

		bool Login(string ime)
		{
			if (_korisnici.ContainsKey(ime))
				return false;

			_korisnici[ime].Add(new Korisnik ()
			{
				Ime = ime;
				LoginTime = DateTime.Now;
				LogoutTime = null;
				LoginSuccess = true;
				LogoutSuccess = false;
			});

			return true;
		}

		bool Logout(string ime)
		{
			if (!_korisnici.ContainsKey(ime))
				return false;

			_korisnici[ime] = new Korisnik()
			{
				Ime = ime;
				LoginTime = _korisnici[ime].LoginTime;
				LogoutTime = DateTime.Now;
				LoginSuccess = _korisnici[ime].LoginSuccess;
				LogoutSuccess = true;
			}

			return true;
		}

		double ProvedenoVreme(string ime)
		{
			if (!_korisnici.ContainsKey(ime))
				return;

			return (_korisnici[ime].LoginTime - _korisnici[ime].LogoutTime).TotalHours;
		}

		List<string> SviKojiSuRadili()
		{
			if (_korisnici.IsEmpty())
				return;

			List<string> ret = new List<string>();

			foreach (Korisnik k in _korisnici.Values)
				if (k.LogoutSuccess)
					ret += "Korisnik: " + k.Ime + " je dosao " + k.LoginTime + ", a otisao je " + k.LogoutTime;
			
			return ret;
		}
	}
}


namespace Jun2019_Client
{
	class Program
	{
		static void Main(string[] args)
		{
			PrijavaClient proxy = new PrijavaClient();

			// korisnik unosi ime
			if (proxy.Login(inputIme))
				Console.Log("Uspesan login.")

			if (proxy.Logout(inputIme))
				Console.Log("Uspesan logout.");

			double provedeno_vreme = proxy.ProvedenoVreme(inputIme);
			Console.Log("Korisnik {0} je proveo {1} sati na poslu", inputIme, provedeno_vreme);
			
			List<string> temp = proxy.SviKojiSuRadili();
			foreach (string s in temp)
				Console.Log(s);			
		}
	}
}