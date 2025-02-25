/*
 * Copyright 2014 http://Bither.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.bither.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.common.primitives.Ints;

import net.bither.BitherApplication;
import net.bither.BitherSetting;
import net.bither.BitherSetting.MarketType;
import net.bither.bitherj.core.BitherjSettings;
import net.bither.bitherj.crypto.PasswordSeed;
import net.bither.bitherj.utils.UnitUtil.BitcoinUnit;
import net.bither.bitherj.utils.Utils;
import net.bither.qrcode.Qr;
import net.bither.util.ExchangeUtil;
import net.bither.util.UnitUtilWrapper;
import net.bither.xrandom.URandom;

import java.util.Currency;
import java.util.Date;
import java.util.Locale;

public class AppSharedPreference {


    private static final String APP_BITHER = "app_bither";
    private static final String PREFS_KEY_LAST_VERSION = "last_version";
    private static final String DEFAULT_MARKET = "default_market";
    private static final String DEFAULT_EXCHANGE_RATE = "default_exchange_rate";

    private static final String LAST_CHECK_PRIVATE_KEY_TIME = "last_check_private_key_time";
    private static final String LAST_BACK_UP_PRIVATE_KEY_TIME = "last_back_up_private_key_time";


    // from service
    private static final String SYNC_BLOCK_ONLY_WIFI = "sync_block_only_wifi";

    private static final String DOWNLOAD_SPV_FINISH = "download_spv_finish";
    private static final String PASSWORD_SEED = "password_seed";
    private static final String USER_AVATAR = "user_avatar";
    private static final String FANCY_QR_CODE_THEME = "fancy_qr_code_theme";
    private static final String FIRST_RUN_DIALOG_SHOWN = "first_run_dialog_shown";

    private static final String APP_MODE = "app_mode";
    private static final String TRANSACTION_FEE_MODE = "transaction_fee_mode";
    private static final String BITHERJ_DONE_SYNC_FROM_SPV = "bitheri_done_sync_from_spv";
    private static final String SYNC_INTERVAL = "sync_interval";

    private static final String PREFS_KEY_LAST_USED = "last_used";

    private static final String PIN_CODE = "pin_code";
    private static final String BITCOIN_UNIT = "bitcoin_unit";
    private static final String XRANDOM_INSTRUCTION_AUTO_SHOW = "xrandom_instruction_auto_show";

    private static AppSharedPreference mInstance = new AppSharedPreference();

    private SharedPreferences mPreferences;

    public static AppSharedPreference getInstance() {
        return mInstance;
    }

    private AppSharedPreference() {
        this.mPreferences = BitherApplication.mContext.getSharedPreferences(APP_BITHER,
                Context.MODE_MULTI_PROCESS);
    }

    public BitherjSettings.AppMode getAppMode() {
        int index = mPreferences.getInt(APP_MODE, -1);
        if (index < 0 || index >= BitherjSettings.AppMode.values().length) {
            return null;
        }
        return BitherjSettings.AppMode.values()[index];
    }

    public void setAppMode(BitherjSettings.AppMode mode) {
        int index = -1;
        if (mode != null) {
            index = mode.ordinal();
        }
        mPreferences.edit().putInt(APP_MODE, index).commit();
    }

    public boolean getBitherjDoneSyncFromSpv() {
        return mPreferences.getBoolean(BITHERJ_DONE_SYNC_FROM_SPV, false);
    }

    public void setBitherjDoneSyncFromSpv(boolean isDone) {
        mPreferences.edit().putBoolean(BITHERJ_DONE_SYNC_FROM_SPV, isDone).commit();

    }

    public BitherjSettings.TransactionFeeMode getTransactionFeeMode() {
        int ordinal = this.mPreferences.getInt(TRANSACTION_FEE_MODE, 0);
        if (ordinal < BitherjSettings.TransactionFeeMode.values().length && ordinal >= 0) {
            return BitherjSettings.TransactionFeeMode.values()[ordinal];
        }
        return BitherjSettings.TransactionFeeMode.Normal;
    }

    public void setTransactionFeeMode(BitherjSettings.TransactionFeeMode mode) {
        if (mode == null) {
            mode = BitherjSettings.TransactionFeeMode.Normal;
        }
        this.mPreferences.edit().putInt(TRANSACTION_FEE_MODE, mode.ordinal()).commit();

    }

    public int getVerionCode() {
        return this.mPreferences.getInt(PREFS_KEY_LAST_VERSION, 0);
    }

    public void setVerionCode(int versionCode) {
        this.mPreferences.edit().putInt(PREFS_KEY_LAST_VERSION, versionCode).commit();
    }

    public MarketType getDefaultMarket() {
        MarketType marketType = getMarketType();
        if (marketType == null) {
            setDefault();
        }
        marketType = getMarketType();
        return marketType;

    }

    private MarketType getMarketType() {
        int type = this.mPreferences.getInt(DEFAULT_MARKET, -1);
        if (type == -1) {
            return null;
        }
        return MarketType.values()[type];

    }

    public void setMarketType(MarketType marketType) {
        this.mPreferences.edit().putInt(DEFAULT_MARKET, marketType.ordinal()).commit();
    }

    private void setDefault() {
        String defaultCountry = Locale.getDefault().getCountry();
        if (Utils.compareString(defaultCountry, "CN") || Utils.compareString
                (defaultCountry, "cn")) {
            setMarketType(MarketType.HUOBI);
        } else {
            setMarketType(MarketType.BITSTAMP);
        }
        String currencyCode = Currency.getInstance(Locale.getDefault()).getCurrencyCode();
        if (Utils.compareString(currencyCode, "CNY")) {
            setExchangeType(ExchangeUtil.Currency.CNY);
        } else if(Utils.compareString(currencyCode, "EUR")) {
            setExchangeType(ExchangeUtil.Currency.EUR);
        }else if(Utils.compareString(currencyCode, "GBP")) {
            setExchangeType(ExchangeUtil.Currency.GBP);
        }else if(Utils.compareString(currencyCode, "JPY")){
            setExchangeType(ExchangeUtil.Currency.JPY);
        }else if(Utils.compareString(currencyCode, "KRW")){
            setExchangeType(ExchangeUtil.Currency.KRW);
        }else if(Utils.compareString(currencyCode, "CAD")){
            setExchangeType(ExchangeUtil.Currency.CAD);
        }else if(Utils.compareString(currencyCode, "AUD")){
            setExchangeType(ExchangeUtil.Currency.AUD);
        }else {
            setExchangeType(ExchangeUtil.Currency.USD);
        }
    }

    public ExchangeUtil.Currency getDefaultExchangeType() {
        ExchangeUtil.Currency currency = getExchangeType();
        if (currency == null) {
            setDefault();
        }
        currency = getExchangeType();
        return currency;

    }

    private ExchangeUtil.Currency getExchangeType() {
        int type = this.mPreferences.getInt(DEFAULT_EXCHANGE_RATE, -1);
        if (type == -1) {
            return null;
        }
        return ExchangeUtil.Currency.values()[type];

    }

    public void setExchangeType(ExchangeUtil.Currency currency) {
        this.mPreferences.edit().putInt(DEFAULT_EXCHANGE_RATE, currency.ordinal()).commit();
    }


    public Date getLastCheckPrivateKeyTime() {
        Date date = null;
        long time = mPreferences.getLong(LAST_CHECK_PRIVATE_KEY_TIME, 0);
        if (time > 0) {
            date = new Date(time);
        }
        return date;
    }

    public void setLastCheckPrivateKeyTime(Date date) {
        if (date != null) {
            mPreferences.edit().putLong(LAST_CHECK_PRIVATE_KEY_TIME, date.getTime()).commit();
        }

    }

    public Date getLastBackupkeyTime() {
        Date date = null;
        long time = mPreferences.getLong(LAST_BACK_UP_PRIVATE_KEY_TIME, 0);
        if (time > 0) {
            date = new Date(time);
        }
        return date;
    }

    public void setLastBackupKeyTime(Date date) {
        if (date != null) {
            mPreferences.edit().putLong(LAST_BACK_UP_PRIVATE_KEY_TIME, date.getTime()).commit();
        }
    }

    public void clear() {
        mPreferences.edit().clear().commit();
    }

    public boolean getSyncBlockOnlyWifi() {
        return mPreferences.getBoolean(SYNC_BLOCK_ONLY_WIFI, false);
    }

    public void setSyncBlockOnlyWifi(boolean onlyWifi) {
        this.mPreferences.edit().putBoolean(SYNC_BLOCK_ONLY_WIFI, onlyWifi).commit();
    }

    public boolean getDownloadSpvFinish() {
        return mPreferences.getBoolean(DOWNLOAD_SPV_FINISH, false);
    }

    public void setDownloadSpvFinish(boolean finish) {
        this.mPreferences.edit().putBoolean(DOWNLOAD_SPV_FINISH, finish).commit();
    }

    public PasswordSeed getPasswordSeed() {
        String str = this.mPreferences.getString(PASSWORD_SEED, "");
        if (Utils.isEmpty(str)) {
            return null;
        }
        return new PasswordSeed(str);
    }

    public void setPasswordSeed(PasswordSeed passwordSeed) {
        this.mPreferences.edit().putString(PASSWORD_SEED, passwordSeed.toPasswordSeedString()).commit();

    }


    public boolean hasUserAvatar() {
        return !Utils.isEmpty(getUserAvatar());
    }

    public String getUserAvatar() {
        return this.mPreferences.getString(USER_AVATAR, "");
    }

    public void setUserAvatar(String avatar) {
        this.mPreferences.edit().putString(USER_AVATAR, avatar).commit();
    }

    public Qr.QrCodeTheme getFancyQrCodeTheme() {
        int index = this.mPreferences.getInt(FANCY_QR_CODE_THEME, 0);
        if (index >= 0 && index < Qr.QrCodeTheme.values().length) {
            return Qr.QrCodeTheme.values()[index];
        }
        return Qr.QrCodeTheme.YELLOW;
    }

    public void setFancyQrCodeTheme(Qr.QrCodeTheme theme) {
        mPreferences.edit().putInt(FANCY_QR_CODE_THEME, theme.ordinal()).commit();
    }

    public boolean getFirstRunDialogShown() {
        return mPreferences.getBoolean(FIRST_RUN_DIALOG_SHOWN, false);
    }

    public void setFirstRunDialogShown(boolean shown) {
        mPreferences.edit().putBoolean(FIRST_RUN_DIALOG_SHOWN, shown).commit();
    }

    public BitherSetting.SyncInterval getSyncInterval() {
        int index = this.mPreferences.getInt(SYNC_INTERVAL,
                BitherSetting.SyncInterval.Normal.ordinal());
        return BitherSetting.SyncInterval.values()[index];
    }

    public void setSyncInterval(BitherSetting.SyncInterval syncInterval) {
        this.mPreferences.edit().putInt(SYNC_INTERVAL, syncInterval.ordinal()).commit();
    }

    public long getLastUsedAgo() {
        final long now = System.currentTimeMillis();
        return now - this.mPreferences.getLong(PREFS_KEY_LAST_USED, 0);
    }

    public void touchLastUsed() {
        final long now = System.currentTimeMillis();
        this.mPreferences.edit().putLong(PREFS_KEY_LAST_USED, now).commit();
    }

    public void setPinCode(CharSequence code) {
        if (code == null || code.length() == 0) {
            deletePinCode();
        }
        int salt = Ints.fromByteArray(new URandom().nextBytes(Ints.BYTES));
        String hash = Integer.toString(salt) + ";" + Integer.toString((code.toString() + Integer
                .toString(salt)).hashCode());
        mPreferences.edit().putString(PIN_CODE, hash).commit();
    }

    public boolean hasPinCode() {
        String hash = mPreferences.getString(PIN_CODE, "");
        if (Utils.isEmpty(hash)) {
            return false;
        }
        String[] strs = hash.split(";");
        if (strs.length != 2) {
            deletePinCode();
            return false;
        }
        return true;
    }

    public void deletePinCode() {
        mPreferences.edit().remove(PIN_CODE).commit();
    }

    public boolean checkPinCode(CharSequence code) {
        if (hasPinCode()) {
            String hash = mPreferences.getString(PIN_CODE, "");
            String[] strs = hash.split(";");
            return Utils.compareString(strs[1], Integer.toString((code.toString() + strs[0])
                    .hashCode()));
        }
        return true;
    }

    public UnitUtilWrapper.BitcoinUnitWrapper getBitcoinUnit() {
        int ordinal = mPreferences.getInt(BITCOIN_UNIT, 0);
        if (ordinal >= 0 && ordinal < BitcoinUnit.values().length) {
            return UnitUtilWrapper.BitcoinUnitWrapper.values()[ordinal];
        } else {
            return UnitUtilWrapper.BitcoinUnitWrapper.BTC;
        }
    }

    public void setBitcoinUnit(UnitUtilWrapper.BitcoinUnitWrapper unit) {
        mPreferences.edit().putInt(BITCOIN_UNIT, unit.ordinal()).commit();
    }

    public boolean shouldAutoShowXRandomInstruction() {
        return mPreferences.getBoolean(XRANDOM_INSTRUCTION_AUTO_SHOW, true);
    }

    public void setAutoShowXRandomInstruction(boolean show) {
        mPreferences.edit().putBoolean(XRANDOM_INSTRUCTION_AUTO_SHOW, show).commit();
    }
}
