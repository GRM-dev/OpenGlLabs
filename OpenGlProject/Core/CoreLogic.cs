using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Input;
using OpenGlProject.Graphic.ViewModel;

namespace OpenGlProject.Core
{
    public class CoreLogic
    {
        private readonly Thread _thread;
        private bool _stop;
        private readonly TpsTimer _timer;

        public CoreLogic(MainAppContext mainAppContext)
        {
            _thread = new Thread(Run);
            AppContext = mainAppContext;
            _timer = new TpsTimer();
        }

        private void Run()
        {
            while (!_stop)
            {
                if (_timer.IsFullCycle())
                {
                    AppContext.Tps = _timer.LastTps;
                }
                _timer.Sync();
            }
        }

        public void Start()
        {
            if (!_thread.IsAlive)
            {
                _timer.InitTime(20);
                _thread.Start();
            }
        }

        public void Stop()
        {
            _stop = true;
            if (_thread.IsAlive)
            {
                Thread.Sleep(100);
                _thread.Abort();
            }
        }

        public MainAppContext AppContext { get; }
    }
}
