using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace OpenGlProject
{
    public class TpsTimer
    {
        private long _lastTickTime;
        private int _currentTicks;
        private long _lastTps;
        private int _skipTicks;
        private int _setTicks;
        private long _lastCallTime;
        private int _sleepTime;

        public void InitTime(int tps)
        {
            _skipTicks = 1000 / tps;
            _setTicks = tps;
            GetDelta();
            _lastCallTime = GetTime();
        }

        public void Sync()
        {
            UpdateTps();
            Wait();
        }

        private void UpdateTps()
        {
            if (IsFullCycle())
            {
                _lastTps = _currentTicks;
                _currentTicks = 0;
                _lastCallTime = GetTime();
            }
            _sleepTime = (int)(_skipTicks - CurrentTicks > 0 ? _skipTicks - CurrentTicks : 0);
            _currentTicks++;
        }

        private void Wait()
        {
            if (_sleepTime > 0)
            {
                try
                {
                    Thread.Sleep(_sleepTime);
                }
                catch (Exception e)
                {
                    Debug.WriteLine(e);
                }
                _sleepTime = 0;
            }
        }

        public bool IsFullCycle()
        {
            return _currentTicks >= _setTicks;
        }

        public int GetDelta()
        {
            long time = GetTime();
            int delta = (int)(time - _lastTickTime);
            _lastTickTime = time;
            return delta;
        }

        public long GetTime()
        {
            return DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond;
        }

        public int CurrentTicks => _currentTicks;
        public long LastTps => _lastTps;
    }
}
