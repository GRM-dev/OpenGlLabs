using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace OpenGlProject.Core
{
    public class CoreLogic
    {
        private readonly Thread _thread;
        private bool _stop;
        private TpsTimer _timer;

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
                AppContext.RotatePyramid += 3.0f;
                AppContext.Rquad -= 3.0f;
                if (_timer.isFullCycle())
                {

                }
                _timer.sync();
            }
        }

        public void Start()
        {
            if (!_thread.IsAlive)
            {
                _timer.initTime(20);
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
