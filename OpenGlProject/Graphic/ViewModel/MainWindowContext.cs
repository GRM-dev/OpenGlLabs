using System;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using SenryakuShuriken.Properties;

namespace SenryakuShuriken.Graphic.ViewModel
{
    public sealed class MainWindowContext : INotifyPropertyChanged
    {
        private string _debug;
        private long _tps;
        private int _renderingObjects;

        public MainWindowContext()
        {
            PropertyChanged += OnPropertyChanged;
        }

        private void OnPropertyChanged(object sender, PropertyChangedEventArgs propertyChangedEventArgs)
        {
            Debug = "TPS: " + Tps+Environment.NewLine;
            Debug += "Rend Objs Count: " + RenderingObjects;
        }

        public string Debug
        {
            get => _debug;
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

        public int RenderingObjects {
            get => _renderingObjects;
            set
            {
                _renderingObjects = value;
                OnPropertyChanged(nameof(RenderingObjects));
            }
        }

        public event PropertyChangedEventHandler PropertyChanged;

        [NotifyPropertyChangedInvocator]
        private void OnPropertyChanged([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }
}