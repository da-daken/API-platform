import React, {useEffect, useRef, useState} from 'react';
import type {FormInstance} from 'antd';
import {Card, Result, Button, Descriptions, Divider, Alert, Statistic, message} from 'antd';
import {PageContainer} from '@ant-design/pro-layout';
import ProForm, {ProFormDigit, ProFormSelect, ProFormText, StepsForm} from '@ant-design/pro-form';
import type {StepDataType} from './data.d';
import styles from './style.less';
import {interfaceNameListUsingGET} from "@/services/heartApi-backend/interfaceInfoController";
import {listTopInterfaceInfoInvokeUsingGET} from "@/services/heartApi-backend/analysisController";
import {payInterfaceUsingPOST} from "@/services/heartApi-backend/userInterfaceInfoController";
import {Simulate} from "react-dom/test-utils";

const StepDescriptions: React.FC<{
  stepData: StepDataType;
  bordered?: boolean;
}> = ({stepData, bordered}) => {
  const {interfaceName, payAccount, num} = stepData;
  return (
    <Descriptions column={1} bordered={bordered}>
      <Descriptions.Item label="接口名称"> {interfaceName}</Descriptions.Item>
      <Descriptions.Item label="充值账户"> {payAccount}</Descriptions.Item>
      <Descriptions.Item label="充值次数">
        <Statistic
          value={num}
          suffix={
            <span
              style={{
                fontSize: 14,
              }}
            >
              次
            </span>
          }
          precision={2}
        />
      </Descriptions.Item>
    </Descriptions>
  );
};

const StepResult: React.FC<{
  onFinish: () => Promise<void>;
}> = (props) => {
  return (
    <Result
      status="success"
      title="操作成功"
      subTitle="已经充值成功！"
      extra={
        <>
          <Button type="primary" onClick={props.onFinish}>
            再充一次
          </Button>
          <Button>查看账单</Button>
        </>
      }
      className={styles.result}
    >
      {props.children}
    </Result>
  );
};

const StepForm: React.FC<Record<string, any>> = () => {
    const [stepData, setStepData] = useState<StepDataType>({
      interfaceName: "",
      payAccount: '',
      receiverName: 'Alex',
      num: '100',
      receiverMode: 'alipay',
    });
    const [current, setCurrent] = useState(0);
    const formRef = useRef<FormInstance>();
    const [interfaceName, setInterfaceName] = useState<Map>();
    const [loading, setLoading] = useState(true);
    const [messageApi, contextHolder] = message.useMessage();



    useEffect(() => {
      try {
        interfaceNameListUsingGET().then(res => {
          if (res.data) {
            setInterfaceName(res.data);
            setLoading(false)
          }
        })
      } catch (e: any) {

      }
    }, [])

    return (
      <PageContainer content="将一个冗长或用户不熟悉的表单任务分成多个步骤，指导用户完成。">
        <Card bordered={false}>
          <StepsForm
            current={current}
            onCurrentChange={setCurrent}
            submitter={{
              render: (props, dom) => {
                if (props.step === 2) {
                  return null;
                }
                return dom;
              },
            }}
          >
            <StepsForm.StepForm<StepDataType>
              formRef={formRef}
              title="填写充值信息"
              initialValues={stepData}
              onFinish={async (values) => {
                console.log(values);
                setStepData(values);
                return true;
              }}
            >
              <ProFormSelect
                label="接口名称"
                width="md"
                name="interfaceName"
                rules={[{required: true, message: '请选择充值的接口'}]}
                valueEnum={
                  interfaceName
                }
              />

              <ProForm.Group title="充值账户" size={8}>
                <ProFormSelect
                  name="receiverMode"
                  rules={[{required: true, message: '请选择充值账户'}]}
                  valueEnum={{
                    alipay: '用户账号',
                  }}
                />
                <ProFormText
                  name="payAccount"
                  rules={[
                    {required: true, message: '请输入充值用户账户'},
                  ]}
                  placeholder="请输入需要充值的账户或ID"
                />
              </ProForm.Group>
              {/*<ProFormText*/}
              {/*  label="收款人姓名"*/}
              {/*  width="md"*/}
              {/*  name="receiverName"*/}
              {/*  rules={[{ required: true, message: '请输入收款人姓名' }]}*/}
              {/*  placeholder="请输入收款人姓名"*/}
              {/*/>*/}
              <ProFormDigit
                label="充值次数"
                name="num"
                width="md"
                rules={[
                  {required: true, message: '请输入充值次数'},
                  {
                    pattern: /[1-9]\d*/,
                    message: '请输入合法数字',
                  },
                ]}
                placeholder="请输入需要充值的次数"
                fieldProps={{
                  prefix: '',
                }}
              />
            </StepsForm.StepForm>

            <StepsForm.StepForm

              title="确认充值信息"
              onFinish={async (values) => {
                console.log(values);
                console.log(formRef.current?.getFieldsValue());
                const params = formRef.current?.getFieldsValue();
                const result = await payInterfaceUsingPOST(params);
                console.log(result);
                if (result.code===40000){
                  message.error("要充值的用户不存在！");
                  return false;
                }
                return true;
              }}
            >
              <div className={styles.result}>
                <Alert
                  closable
                  showIcon
                  message="确认充值后，接口次数将直接添加到用户账户。"
                  style={{marginBottom: 24}}
                />
                <StepDescriptions stepData={stepData} bordered/>
                <Divider style={{margin: '24px 0'}}/>
                <ProFormText.Password
                  label="管理员密码"
                  width="md"
                  name="password"
                  required={false}
                  rules={[{required: true, message: '需要管理员密码才能进行支付'}]}
                />
              </div>
            </StepsForm.StepForm>
            <StepsForm.StepForm title="完成">
              <StepResult
                onFinish={async () => {
                  setCurrent(0);
                  formRef.current?.resetFields();
                }}
              >
                <StepDescriptions stepData={stepData}/>
              </StepResult>
            </StepsForm.StepForm>
          </StepsForm>
          <Divider style={{margin: '40px 0 24px'}}/>
          <div className={styles.desc}>
            <h3>说明</h3>
            <h4>充值到用户账户</h4>
            <p>
              只有管理员可以充值，账号要输入正确，若查询不到该账号会无法继续充值。
            </p>

          </div>
        </Card>
      </PageContainer>
    );
  }
;

export default StepForm;
