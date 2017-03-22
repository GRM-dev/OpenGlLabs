using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OpenGlProject
{
    public class TpsTimer
    {
        private long lastTick;
        private int tpsT = 0;
        private long lastCallTime;
        private long lastTPS = 0;
        private int tps;

        public void initTime(int tps)
        {
            this.tps = tps;
            getDelta();
            lastCallTime = getTime();
        }

        public void sync()
        {
            updateTPS();
            Wait();
        }

        private void updateTPS()
        {
            long currentTime = getTime();
            if (currentTime - lastCallTime > 1000)
            {
                lastTPS = tpsT;
                tpsT = 0;
                lastCallTime += 1000;
            }
            tpsT++;
        }

        private void Wait()
        {

        }

        public bool isFullCycle()
        {
            return getLastTps() == getTPS() || (getTime() - lastCallTime > 1000);
        }

        public int getDelta()
        {
            long time = getTime();
            int delta = (int)(time - lastTick);
            lastTick = time;
            return delta;
        }

        public long getTime()
        {
            return DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond;
        }

        public int getTPS()
        {
            return tpsT;
        }

        public long getLastTps()
        {
            return lastTPS;
        }
    }
}
