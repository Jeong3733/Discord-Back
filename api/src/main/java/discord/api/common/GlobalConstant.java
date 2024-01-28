package discord.api.common;

public class GlobalConstant {
    /**
     * Access Token 만료 시간 (6시간 = 6h * 60m * 60s * 1000ms)
     */
    public static final Long ACCESS_EXP_TIME = 6 * 60 * 60 * 1000L;

    /**
     * Refresh Token 만료 시간 (48시간 = 24h * 60m * 60s * 1000ms)
     */
    public static final Long REFRESH_EXP_TIME = 2 * 24 * 60 * 60 * 1000L;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_HEADER = "Authorization";
}
