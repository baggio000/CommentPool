import com.jcraft.jsch.UserInfo;

public class PublicKeyAuthUserInfo implements UserInfo
{

    public PublicKeyAuthUserInfo(final String passphrase, final String password)
    {
        super();
        this.passphrase = passphrase;
        this.password = password;
    }

    private String passphrase;
    private String password;

    @Override
    public String getPassphrase()
    {
        return this.passphrase;
    }

    @Override
    public String getPassword()
    {
        return this.password;
    }

    @Override
    public boolean promptPassword(final String s)
    {
        return false;
    }

    @Override
    public boolean promptPassphrase(final String s)
    {
        return true;
    }

    @Override
    public boolean promptYesNo(final String s)
    {
        // ?

        // trust
        return true;
    }

    @Override
    public void showMessage(final String s)
    {
        // log message
    }

    public void setPassphrase(final String passphrase)
    {
        this.passphrase = passphrase;
    }

    public void setPassword(final String password)
    {
        this.password = password;
    }
}