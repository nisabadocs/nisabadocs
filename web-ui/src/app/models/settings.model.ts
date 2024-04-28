// application-setting.model.ts

export interface ApplicationSetting {
  id?: string; // UUID string
  settingKey?: string;
  settingValue?: string;
  settingType?: string;
  updatedAt?: Date;
}
