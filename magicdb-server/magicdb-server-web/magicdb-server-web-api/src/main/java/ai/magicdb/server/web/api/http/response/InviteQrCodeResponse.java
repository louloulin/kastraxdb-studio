package ai.magicdb.server.web.api.http.response;

import lombok.Data;

@Data
public class InviteQrCodeResponse {

    private String wechatQrCodeUrl;

    private String tip;

}
