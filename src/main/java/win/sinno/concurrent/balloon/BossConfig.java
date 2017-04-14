package win.sinno.concurrent.balloon;

/**
 * Boss config
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/14 16:25
 */
public class BossConfig {
    public int maxTotal = 8;

    private int maxIdle = 8;

    private int minIdle = 0;

    private boolean blockWhenExhausted = true;

    private long checkTs = 1000 * 30;

    private boolean isSoftCheck = true;

    private long checkIdleTs = 1000 * 60 * 10;

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public boolean isBlockWhenExhausted() {
        return blockWhenExhausted;
    }

    public void setBlockWhenExhausted(boolean blockWhenExhausted) {
        this.blockWhenExhausted = blockWhenExhausted;
    }

    public long getCheckTs() {
        return checkTs;
    }

    public void setCheckTs(long checkTs) {
        this.checkTs = checkTs;
    }

    public boolean isSoftCheck() {
        return isSoftCheck;
    }

    public void setSoftCheck(boolean softCheck) {
        isSoftCheck = softCheck;
    }

    public long getCheckIdleTs() {
        return checkIdleTs;
    }

    public void setCheckIdleTs(long checkIdleTs) {
        this.checkIdleTs = checkIdleTs;
    }
}
