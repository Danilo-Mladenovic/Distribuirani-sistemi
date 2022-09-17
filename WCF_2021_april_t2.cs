using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.Text;

namespace April2021
{
    [ServiceContract]
    public interface IRegistracijaVozila
    {
        [OperationContract]
        void Registracija(Vlasnik vlasnik, Vozilo vozilo);

        [OperationContract]
        List<Vozilo> VozilaVlasnika(Vlasnik vlasnik);

        [OperationContract]
        List<Vlasnik> VlasniciModela(string molel);

        [OperationContract]
        List<Vozilo> SvaVozila();
    }


    [DataContract(IsReference = true)]
    public class Vlasnik
    {
        [DataMember]
        public string Ime { get; set; }
        [DataMember]
        public string Prezime { get; set; }
        [DataMember]
        public string Jmbg { get; set; }
        [DataMember]
        List<Vozilo> Vozila { get; set; }

        public Vlasnik()
        {
            this.Vozila = new List<Vozilo>();
        }
    }


    [DataContract(IsReference = true)]
    public class Vozilo
    {
        [DataMember]
        public string Marka { get; set; }
        [DataMember]
        public string Model { get; set; }
        [DataMember]
        public string boja { get; set; }
        [DataMember]
        public Vlasnik Vlasniq { get; set; }

        public Vozilo()
        {
            this.Vlasniq = null;
        }
    }



    [ServiceBehavior(InstanceContextMode = InstanceContextMode.Single)]
    public class RegistracijaVozila : IRegistracijaVozila
    {
        private Dictionary<string, Vlasnik> vlasnici;

        public RegistracijaVozila()
        {
            this.vlasnici = new Dictionary<string, Vlasnik>();
        }


        public void Registracija(Vlasnik vlasnik, Vozilo vozilo)
        {

        }

        public List<Vozilo> VozilaVlasnika(Vlasnik vlasnik)
        {

        }

        public List<Vlasnik> VlasniciModela(string molel)
        {

        }

        public List<Vozilo> SvaVozila()
        {

        }
    }
}



namespace April2021Klijent
{
    public class Program
    {
        static void Main(string[] args)
        {
            RegistracijaVozilaKlijent proxy = new RegistracijaVozilaKlijent();

            Console.ReadLine();
            proxy.Registracija(new Vlasnik()
            {
                Ime = Console.ReadLine().Trim(),
                Jmbg = Console.ReadLine().Trim()
            }, new Vozilo()
            {
                Marka = Console.ReadLine().Trim(),
                Model = Console.ReadLine().Trim()
            });


        }
    }
}