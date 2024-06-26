<template>
    <accordion show-checkbox show-content>
        <header class="var-header" slot="header">
            <span>{{ $t('editPage.stageOption') }}</span>
            <i class="bk-icon icon-angle-down" style="display:block"></i>
        </header>
        <div slot="content" class="bk-form bk-form-vertical">
            <bk-form :label-width="200" form-type="vertical">
                <form-field>
                    <bk-checkbox :disabled="disabled" v-model="stageEnable">
                        {{ $t('enableStage') }}
                    </bk-checkbox>
                </form-field>
                <section v-if="!isFinally">
                    <form-field>
                        <bk-checkbox :disabled="disabled" v-model="stageFastKill">
                            {{ $t('stageFastKill') }}
                        </bk-checkbox>
                        <i v-bk-tooltips="$t('stageFastKillDesc')" class="bk-icon icon-info-circle" />
                    </form-field>
                    <bk-form-item :label="$t('stageOptionLabel')">
                        <bk-select :disabled="disabled" v-model="stageCondition" searchable>
                            <bk-option v-for="option in conditionConf"
                                :key="option.id"
                                :id="option.id"
                                :name="option.name">
                            </bk-option>
                        </bk-select>
                    </bk-form-item>
                    <bk-form-item v-if="showVariable">
                        <key-value-normal :disabled="disabled" :value="variables" :allow-null="false" name="customVariables" :handle-change="handleUpdateStageControl"></key-value-normal>
                    </bk-form-item>
                    <bk-form-item v-if="showCondition" :label="$t('storeMap.customConditionExp')">
                        <vuex-input
                            :value="customConditionExpress"
                            name="customCondition"
                            :handle-change="handleUpdateStageControl"
                        >
                        </vuex-input>
                    </bk-form-item>

                </section>
            </bk-form>
        </div>
    </accordion>
</template>

<script>
    import { mapActions } from 'vuex'
    import Accordion from '@/components/atomFormField/Accordion'
    import VuexInput from '@/components/atomFormField/VuexInput'
    import KeyValueNormal from '@/components/atomFormField/KeyValueNormal'
    import FormField from '@/components/AtomPropertyPanel/FormField'

    export default {
        name: 'stage-control',
        components: {
            Accordion,
            KeyValueNormal,
            FormField,
            VuexInput
        },
        props: {
            stageControl: {
                type: Object,
                default: () => ({})
            },
            disabled: {
                type: Boolean,
                default: false
            },
            isFinally: {
                type: Boolean,
                default: false
            },
            handleStageChange: {
                type: Function,
                required: true
            }
        },
        computed: {
            stageEnable: {
                get () {
                    return this.stageControl.enable
                },
                set (enable) {
                    this.handleUpdateStageControl('enable', enable)
                }
            },
            stageFastKill: {
                get () {
                    return this.stageControl.fastKill
                },
                set (fastKill) {
                    this.handleStageChange('fastKill', fastKill)
                }
            },
            stageCondition: {
                get () {
                    return this.stageControl.runCondition
                },
                set (runCondition) {
                    this.handleUpdateStageControl('runCondition', runCondition)
                }
            },
            variables () {
                return Array.isArray(this.stageControl?.customVariables) ? this.stageControl?.customVariables : []
            },
            customConditionExpress () {
                return this.stageControl?.customCondition ?? ''
            },
            conditionConf () {
                return [
                    {
                        id: 'AFTER_LAST_FINISHED',
                        name: this.$t('storeMap.afterPreStageSuccess')
                    },
                    {
                        id: 'CUSTOM_VARIABLE_MATCH',
                        name: this.$t('storeMap.varMatch')
                    },
                    {
                        id: 'CUSTOM_CONDITION_MATCH',
                        name: this.$t('storeMap.customCondition')
                    },
                    {
                        id: 'CUSTOM_VARIABLE_MATCH_NOT_RUN',
                        name: this.$t('storeMap.varNotMatch')
                    }
                ]
            },
            showVariable () {
                return ['CUSTOM_VARIABLE_MATCH', 'CUSTOM_VARIABLE_MATCH_NOT_RUN'].indexOf(this.stageCondition) > -1
            },
            showCondition () {
                return ['CUSTOM_CONDITION_MATCH'].indexOf(this.stageCondition) > -1
            }
        },
        watch: {
            showVariable (val) {
                !val && this.handleUpdateStageControl('customVariables', [{ key: 'param1', value: '' }])
            }
        },
        created () {
            if (!this.disabled) {
                this.initStageControl()
            }
        },
        methods: {
            ...mapActions('atom', [
                'setPipelineEditing'
            ]),
            handleUpdateStageControl (name, value) {
                this.setPipelineEditing(true)
                this.handleStageChange('stageControlOption', {
                    ...(this.stageControl || {}),
                    [name]: value
                })
            },
            initStageControl () {
                if (this.stageControl === undefined || JSON.stringify(this.stageControl) === '{}') {
                    this.handleStageChange('stageControlOption', {
                        enable: true,
                        runCondition: 'AFTER_LAST_FINISHED',
                        customCondition: '',
                        customVariables: [{ key: 'param1', value: '' }],
                        manualTrigger: false,
                        triggerUsers: [],
                        timeout: 24
                    })
                    this.handleStageChange('fastKill', false)
                }
            }
        }
    }
</script>

<style lang="scss">
    .stage-trigger-member-input.is-error,
    .stage-timeout-input.is-error {
        color: #ff5656;
    }
</style>
