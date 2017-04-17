package win.sinno.concurrent.balloon;

/**
 * default boss config
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/17 10:03
 */
public class DefaultBossConfig extends BossConfig {

    public DefaultBossConfig() {
        setMaxTotal(8);
        setMaxIdle(8);
        setMinIdle(2);
        setBlockWhenExhausted(true);
        setCheckTs(1000 * 60);
        setCheckIdleTs(1000 * 60 * 30);
        setSoftCheck(true);
    }
}
