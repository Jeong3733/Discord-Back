package discord.api.common;

public class GlobalConstant {
    /**
     * Access Token 만료 시간 (15분 = 15m * 60s * 1000ms)
     */
    public static final Long ACCESS_EXP_TIME = 15 * 60 * 1000L;

    /**
     * Refresh Token 만료 시간 (2시간 = 2h * 60m * 60s * 1000ms)
     */
    public static final Long REFRESH_EXP_TIME = 2 * 60 * 60 * 1000L;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_HEADER = "Authorization";
}
