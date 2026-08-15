<script setup lang="ts">
// Icons
import { Pencil, Plus } from '@lucide/vue';

// Models
import type { UserAccount } from '../api/user';

// APIs
import { getUserApi } from '../api/user';
import { listBankAccountsApi, deleteBankAccountApi, type BankAccount } from '../api/bank';
import AddBankAccountModal from '../components/AddBankAccountModal.vue';
import EditBankAccountModal from '../components/EditBankAccountModal.vue';
import BankAccountCard from '../components/BankAccountCard.vue';
import { onMounted, ref } from 'vue';

const userProfile = ref<UserAccount | null>(null);
const bankAccounts = ref<BankAccount[]>([]);
const showAddModal = ref(false);
const editingAccount = ref<BankAccount | null>(null);

const loadBankAccounts = async () => {
  try {
    const response = await listBankAccountsApi();
    bankAccounts.value = response.data.content;
  } catch {
    bankAccounts.value = [];
  }
};

const onBankAccountSaved = () => {
  showAddModal.value = false;
  loadBankAccounts();
};

const onBankAccountUpdated = () => {
  editingAccount.value = null;
  loadBankAccounts();
};

const handleDelete = async (account: BankAccount) => {
  if (!confirm(`Delete ${account.nickName || account.bank.shortName}?`)) return;
  try {
    await deleteBankAccountApi(account.id);
    loadBankAccounts();
  } catch {
    // Ignore for now; could surface an error
  }
};

onMounted(async () => {
    userProfile.value = (await getUserApi()).data;
    loadBankAccounts();
});

</script>

<template>
    <div class="profile-page">
        <div>
            <div class="page-title">Profile & Settings</div>
            <div class="page-intent">Manage your account and preferences</div>
        </div>
        <div class="row g-3 profile-page-content">
            <!-- Account details -->
            <div class="col-12 col-lg-4">
                <div class="account-details">
                    <div class="thumbnail-circle"><div class="user-thumbnail">DC</div></div>
                    <div class="user-fullname">Dileep Chandran</div>
                    <div class="user-email">dileep.chandran.dc.dc@gmail.com</div>
                    <hr class="divider">
                    <div class="account-details-content">
                        <div class="account-details-content-item">
                            <div class="account-details-category">User Since</div>
                            <div class="account-details-value">10th Aug 2026</div>
                        </div>
                        <div class="account-details-content-item">
                            <div class="account-details-category">Currency</div>
                            <div class="account-details-value">INR</div>
                        </div>
                        <div class="account-details-content-item">
                            <div class="account-details-category">Time Zone</div>
                            <div class="account-details-value">IST (UTC+5:30)</div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-12 col-lg-8">
                <div class="profile-page-main-data">
                    <!-- Personal details -->
                    <div class="profile-details">
                        <div class="account-details-header">
                            <div class="account-details-header-label">Personal Details</div>
                            <button class="account-details-header-edit-button"><Pencil :size="17"/><span>Edit</span></button>
                        </div>
                        <div v-if="userProfile" class="profile-details-content">
                            <div class="profile-details-content-item">
                                <div class="profile-details-content-item-label">Fullname</div>
                                <div class="profile-details-content-item-value">{{ userProfile.firstName + (userProfile.lastName != null ? ' ' +  userProfile.lastName : '') }}</div>
                            </div>
                            <div class="profile-details-content-item">
                                <div class="profile-details-content-item-label">Email</div>
                                <div class="profile-details-content-item-value">{{ userProfile.email }}</div>
                            </div>
                            <div class="profile-details-content-item">
                                <div class="profile-details-content-item-label">Mobile</div>
                                <div class="profile-details-content-item-value">{{ userProfile.mobile != null && userProfile.countryCode != null ? userProfile.countryCode + ' ' + userProfile.mobile : '' }}</div>
                            </div>
                        </div>
                    </div>

                    <div class="payment-channels-details">
                        <div class="payment-channels-header">
                            <div class="payment-channels-header-label">Payment Channels</div>
                            <button class="payment-channels-header-add" @click="showAddModal = true"><Plus :size="15"/><span>Add</span></button>
                        </div>
                        <div class="payment-channels-subheading">Bank Accounts</div>
                        <div class="payment-channels-content">
                            <div v-if="bankAccounts.length === 0" class="payment-channels-empty">
                                No payment channels added yet.
                            </div>
                            <BankAccountCard
                                v-for="account in bankAccounts"
                                :key="account.id"
                                :account="account"
                                @edit="editingAccount = $event"
                                @delete="handleDelete"
                            />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <AddBankAccountModal
        v-if="showAddModal"
        @close="showAddModal = false"
        @saved="onBankAccountSaved"
    />

    <EditBankAccountModal
        v-if="editingAccount"
        :account="editingAccount"
        @close="editingAccount = null"
        @saved="onBankAccountUpdated"
    />
</template>

<style scoped>
.payment-channels-header {
    display: flex;
    justify-content: space-between;
    width: 100%;
    align-items: center;
}
.payment-channels-header-label {
    font-size: small;
    font-weight: 600;
    color: var(--black, #000000);
}
.payment-channels-header-add {
    display: flex;
    align-items: center;
    color: rgb(79, 70, 229);
    background-color: rgb(79, 70, 229, 0.1);
    border: 0px;
    border-radius: 10px;
    padding: 5px 10px;
    font-size: small;
    gap: 5px;
    cursor: pointer;
}

.payment-channels-subheading {
    width: 100%;
    color: grey;
    font-size: small;
    font-weight: 500;
}

.profile-page {
    padding: 10px;
}

.page-title {
    color: black;
    font-weight: 700;
    font-size: medium;
}

.page-intent {
    color: gray;
    font-weight: normal;
    font-size: small;
}

.profile-page-content {
    margin-top: 20px;
}

/* Thumbnail styles */
.thumbnail-circle {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background-color: rgb(79, 70, 229);
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 700;
  border: 2px solid rgb(79, 70, 229);
  flex-shrink: 0;
}

.user-thumbnail {
    color: #ffffff;
    font-size: medium;
}

.user-fullname {
    color: black;
    font-size: medium;
    font-weight: 700;
}

.user-email {
    color: grey;
    font-size: smaller;
}

/* Account data styles */
.account-details,
.profile-details,
.payment-channels-details {
    background-color: #ffffff;
    border-radius: 8px;
    padding: 20px;
    height: 100%;
}
.account-details {
    display: flex;
    flex-direction: column;
    gap: 10px;
    align-items: center;
}
.account-details-content {
    display: flex;
    flex-direction: column;
    width: 100%;
    gap: 10px;
}
.account-details-content-item {
    display: flex;
    width: 100%;
    justify-content: space-between;
}
.account-details-category {
    color: grey;
    font-size: small;
}
.account-details-value {
    color: black;
    font-size: small;
}
.divider {
  border-top: 1px #000000;
  margin: 15px 5px;
  width: 100%;
}
.account-details-header {
    display: flex;
    width: 100%;
    justify-content: space-between;
    align-items: center;
}
.account-details-header-label {
    font-size: small;
    font-weight: 700;
    color: #000000;
}
.account-details-header-edit-button {
    display: flex;
    gap: 5px;
    align-items: center;
    background-color: transparent;
    border: 0px;
    color: rgb(79, 70, 229);
    cursor: pointer;
}

/* Main content area style */
.profile-page-main-data {
    display: flex;
    flex-direction: column;
    width: 100%;
    gap: 20px;
}

/* Profile details styles */
.profile-details {
    display: flex;
    flex-direction: column;
    gap: 10px;
    align-items: center;
}
.profile-details-content {
    display: flex;
    flex-direction: column;
    gap: 5px;
    width: 100%;
}
.profile-details-content-item {
    display: flex;
    width: 100%;
    justify-content: space-between;
}
.profile-details-content-item-label {
    color: grey;
    font-size: small;
}
.profile-details-content-item-value {
    color: black;
    font-size: small;
}

/* Payment Channels styles */
.payment-channels-details {
    display: flex;
    flex-direction: column;
    gap: 10px;
    align-items: center;
}
.payment-channels-content {
    display: flex;
    flex-direction: column;
    gap: 8px;
    width: 100%;
}
.payment-channels-empty {
    color: grey;
    font-size: small;
    padding: 12px 0;
}
</style>