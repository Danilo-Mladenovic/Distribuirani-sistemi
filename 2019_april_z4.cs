<services>
  <service name="April2021.Registracija">
    <endpoint contract="April2021.IRegistracija" binding="basicHttpBinding" />
  </service>
</services>

namespace April2021
{
	[ServiceContract]
	public interface IRegistracija
	{
		[OperationContract]
		void Registruj(Vlasnik vlasnik, Vozila vozilo);

		[OperationContract]
		List<Vozilo> VratiVozilaVlasnika(string vlasnik);

		[OperationContract]
		List<Vlasnik> VratiVlasnikeModela(string model);

		[OperationContract]
		List<Vozilo> VratiSvaVozila();
	}


	[DataContract(IsReference=true)]
	public class Vlasnik 
	{
		[DataMember]
		public string Ime { get; set; }

		[DataMember]
		public string Prezime { get; set; }

		[DataMember]
		public int Jmbg { get; set; }

		[DataMember]
		public List<Vozilo> Vozila { get; set; }

		public Vlasnik()
		{
			Vozila = new List<Vozilo>();
		}

		public override bool Equals(object obj)
		{
			return obj is Vlasnik vlasnik &&
					Jmbg == obj.Jmbg;
		}
	}


	[DataContract(IsReference=true)]
	public class Vozilo 
	{
		[DataMember]
		public string Marka { get; set; }

		[DataMember]
		public string Model { get; set; }

		[DataMember]
		public string Boja { get; set; }

		[DataMember]
		public Vlasnik Vlasnik { get; set; }

		public Vozilo()
		{
			Vlasnik = null;
		}

		public override bool Equals(Object obj)
		{
			return obj is Vozilo vozilo &&
					Marka == obj.Marka &&
					Model == obj.Model &&
					Boja == obj.Boja;
		}
	}


	[ServiceBehavior(InstanceContextMode=InstanceContextMode.Single)]
	public class Registracija : IRegistracija 
	{
		private Dicionary<string, Vlasnik> _vlasnici;

		public Registracija()
		{
			_vlasnici = new Dicionary<string, Vlasnik>();
		}

		public void Registruj(Vlasnik vlasnik, Vozila vozilo)
		{
			if (vlasnik == null || vozilo == null)
				return;

			if (!_vlasnici.ContainsKey(vlasnik.Jmbg))
				_vlasnici.Add(vlasnik.jmbg, new Vlasnik()
				{
					Ime = vlasnik.Ime;
					Prezime = vlasnik.Prezime;
					Jmbg = vlasnik.Jmbg;
					Vozila = new List<Vozilo>();
				});

			if (!_vlasnici[vlasnik.jmbg].Vozila.Contains(vozilo))
				_vlasnici[vlasnik.jmbg].Vozila.Add(new Vozilo()
				{
					Marka = vozilo.Marka;
					Model = vozilo.Model;
					Boja = vozilo.Boja;
					Vlasnik = _vlasnici[vlasnik.jmbg];
				});
		}

		public List<Vozilo> VratiVozilaVlasnika(string jmbg)
		{
			if (!_vlasnici.ContainsKey(jmbg))
				return;

			List<Vozilo> ret = new List<Vozilo>();

			foreach (Vozilo v in _vlasnici[jmbg].Vozila)
				ret.Add(v);

			return ret;
		}

		public List<Vlasnik> VratiVlasnikeModela(string model)
		{
			List<Vlasnik> ret = new List<Vlasnik>();

			foreach (Vlasnik vl in _vlasnici.Values)
				foreach(Vozilo vozilo in vl.Vozila)
					if (vozilo.Model == model)
						ret.Add(vl);

			return ret;
		}

		public List<Vozilo> VratiSvaVozila()
		{
			if (_vlasnici.IsEmpty())
				return false;

			List<Vozilo> ret = new List<Vozilo>();

			foreach (Vlasnik vl in _vlasnici.Values)
				foreach (Vozilo vozilo in vl.Vozila)
					ret.Add(vozilo);

			return ret;
		}
	}
}


namespace April2021_Client
{
	class Program
	{
		static void Main(string[] args)
		{
			RegistracijaClient proxy = new RegistracijaClient();

			Vlasnik vl = new Vlasnik()
			{
				Ime = inputIme;
				Prezime = inputPrezime;
				Jmbg = inputJmbg;
				Vozila = new List<Vozila>();
			}

			Vozilo vozilo = new Vozilo()
			{
				Marka = inputMarka;
				Model = inputModel;
				Boja = inputBoja;
				Vlasnik = vl;
			}

			proxy.Registracija(vl, vozilo);

			// unosi se jmbg vlasnika za sledecu operaciju
			List<Vozilo> lista_vozila = proxy.VratiVozilaVlasnika(inputJmbg);

			// unosi se model za sledecu operaciju
			List<Vlasnik> lista_vlasnika = proxy.VratiVlasnikeModela(inputModel);

			List<Vozilo> lista_svih_vozila = proxy.VratiSvaVozila();

			proxy.Close();
		}
	}
}