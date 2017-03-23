using System;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using OpenGlProject.Annotations;

namespace OpenGlProject
{
    public sealed class MainAppContext : INotifyPropertyChanged
    {
        private float _rquad;
        private float _rotatePyramid;
        private float _dr = 0;
        private float _dq = 0;
        private string _debug;
        private long _tps;

        public MainAppContext()
        {
            PropertyChanged += OnPropertyChanged;
        }

        private void OnPropertyChanged(object sender, PropertyChangedEventArgs propertyChangedEventArgs)
        {
            Debug = "TPS: " + Tps;
            Debug += "\nDR = " + Dr;
            Debug += "\nDQ = " + Dq;
        }

        public void UpdateView()
        {

        }

        public float Rquad
        {
            get => _rquad;
            set
            {
                _rquad = value;
                OnPropertyChanged(nameof(Rquad));
            }
        }

        public float RotatePyramid
        {
            get => _rotatePyramid;
            set
            {
                _rotatePyramid = value;
                OnPropertyChanged(nameof(RotatePyramid));
            }
        }

        public string Debug
        {
            get { return _debug; }
            set { _debug = value; }
        }

        public long Tps
        {
            get => _tps;
            set
            {
                _tps = value;
                OnPropertyChanged(nameof(Tps));
            }
        }

        public float Dr
        {
            get { return _dr; }
            set { _dr = value; }
        }

        public float Dq
        {
            get { return _dq; }
            set { _dq = value; }
        }

        public event PropertyChangedEventHandler PropertyChanged;

        [NotifyPropertyChangedInvocator]
        private void OnPropertyChanged([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }
}